package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.config.conf.OperationalDataConfig;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.OperationalDataFilterDao;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class OperationalDataFilterService extends BaseRepositoryService<OperationalDataFilter> {


    private final OperationalDataConfig defaultFilter;
    private final OperationalDataFilterDao filterDao;

    public OperationalDataFilterService(OperationalDataConfig defaultFilter, OperationalDataFilterDao filterDao) {
        super(null);
        this.defaultFilter = defaultFilter;
        this.filterDao = filterDao;
    }

    /**
     *
     * @return default global filter configured by deployment parameters
     */
    public OperationalDataFilter getDefaultGlobalFilter(){
        OperationalDataFilter filter = new OperationalDataFilter();
        filter.setMinOperationalHours(defaultFilter.getMinOperationalHours());
        return filter;
    }

    /**
     *
     * @param systemURI
     * @return the aircraft operational data filter, if no filter is associated with the provided systemURI in
     * persistent storage the current global filter is returned.
     */
    public OperationalDataFilter getSystemFilter(URI systemURI){
        OperationalDataFilter filter = filterDao.findByEntity(systemURI);
        if(filter == null){
            filter = new OperationalDataFilter();
            filter.setAs(getDefaultGlobalFilter());
        }

        return filter;
    }

    /**
     *
     * @param faultTreeURI
     * @param systemURI
     * @return operational data filter associated with faultTreeURI in persistent storage, if not present returns
     * getSystemFilter(systemURI)
     */

    public OperationalDataFilter getFaultTreeFilter(URI faultTreeURI, URI systemURI){
        OperationalDataFilter filter = filterDao.findByEntity(faultTreeURI);
        if(filter == null){
            filter = new OperationalDataFilter();
            filter.setAs(getSystemFilter(systemURI));
        }

        return filter;
    }

    @Transactional
    public void updateSystemFilter(URI systemURI, OperationalDataFilter newFilter){
        URI context = getToolContext(systemURI);
        updateFilter(systemURI, newFilter, context);
    }

    @Transactional
    public void updateFaultTreeFilter(URI faultTreeURI, OperationalDataFilter newFilter){
        updateFilter(faultTreeURI, newFilter, faultTreeURI);
    }

    public void updateFilter(URI entity, OperationalDataFilter newFilter, URI context){

        Objects.requireNonNull(newFilter);
        OperationalDataFilter filter = filterDao.findByEntity(entity);
        if(filter != null) {
            filter.setContext(context);
            filterDao.update(filter);
            filter.setAs(newFilter);
            return;
        }
        filter = new OperationalDataFilter();
        filter.setAs(newFilter);
        filter.setContext(context);
        persist(filter);
        filterDao.persistHasFilter(entity, filter);
    }

    public void removeFilter(){
        List<OperationalDataFilter> filters = findAll();
        if(filters.isEmpty())
            return;
        for(OperationalDataFilter filter: filters)
            filterDao.remove(filter);
    }

    @Override
    protected GenericDao<OperationalDataFilter> getPrimaryDao() {
        return filterDao;
    }

    @Override
    protected void validate(OperationalDataFilter instance) {
    }
}
