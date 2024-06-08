package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.ManagedEntity;
import cz.cvut.kbss.analysis.model.UserReference;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import cz.cvut.kbss.jopa.vocabulary.DC;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class ManagedEntityDao<T extends ManagedEntity> extends NamedEntityDao<T>{

    public static URI P_CREATED = URI.create(DC.Terms.CREATED);
    public static URI P_MODIFIED = URI.create(DC.Terms.MODIFIED);
    public static URI P_CREATOR = URI.create( DC.Terms.CREATOR);
    public static URI P_LAST_EDITOR = URI.create(Vocabulary.s_c_editor);

    protected final SecurityUtils securityUtils;

    protected ManagedEntityDao(Class<T> type, EntityManager em, PersistenceConf config, IdentifierService identifierService, SecurityUtils securityUtils) {
        super(type, em, config, identifierService);
        this.securityUtils = securityUtils;
    }



    @Override
    protected void setEntityDescriptor(EntityDescriptor descriptor) {
        EntityType<ManagedEntity> ft = em.getMetamodel().entity(ManagedEntity.class);
        Attribute creator = ft.getAttribute("creator");
        Attribute lastEditor = ft.getAttribute("lastEditor");

        descriptor.addAttributeContext(creator, null);
        descriptor.addAttributeContext(lastEditor, null);
        super.setEntityDescriptor(descriptor);
    }

    public void setChangedByContext(URI context, Date date){
        UserReference user = securityUtils.getCurrentUserReference();
        em.createNativeQuery("""
                DELETE{
                    GRAPH ?context{ 
                        ?uri ?pModified ?lastModified. 
                        ?uri ?pLastEditor ?lastEditorURI. 
                    }
                }INSERT{
                    GRAPH ?context{
                            ?uri ?pModified ?newModified. 
                            ?uri ?pLastEditor ?newLastEditor. 
                        }
                }WHERE{
                    GRAPH ?context{
                        ?uri a ?type.
                        OPTIONAL{?uri ?pModified ?lastModified.} 
                        OPTIONAL{?uri ?pLastEditor ?lastEditorURI.} 
                    }
                }""")
                .setParameter("context", context)
                .setParameter("type", typeUri)
                .setParameter("pModified", P_MODIFIED)
                .setParameter("pLastEditor", P_LAST_EDITOR)
                .setParameter("newModified", date)
                .setParameter("newLastEditor", user.getUri())
                .executeUpdate();
    }

    public List<T> findAllSummaries(){
        try {
            List<ManagedEntity> ret = em.createNativeQuery("""
                            SELECT * WHERE {
                                ?uri a ?type.
                                ?uri ?pName ?name.
                                OPTIONAL{?uri ?pDescription ?description.}
                                OPTIONAL{?uri ?pCreated ?created.}
                                OPTIONAL{?uri ?pModified ?modified.}
                                OPTIONAL{?uri ?pCreator ?creator.}
                                OPTIONAL{?uri ?pLastEditor ?lastEditor.}
                            }""", "ManagedEntitySummary")
                    .setParameter("type", typeUri)
                    .setParameter("pName", P_HAS_NAME)
                    .setParameter("pDescription", P_HAS_DESCRIPTION)
                    .setParameter("pCreated", P_CREATED)
                    .setParameter("pModified", P_MODIFIED)
                    .setParameter("pCreator", P_CREATOR)
                    .setParameter("pLastEditor", P_LAST_EDITOR)
                    .getResultList();

            return ret.stream().map(s -> s.asEntity(type)).toList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }



}
