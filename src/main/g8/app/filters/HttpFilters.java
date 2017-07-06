package filters;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.filters.headers.SecurityHeadersFilter;
import play.filters.hosts.AllowedHostsFilter;
import play.http.DefaultHttpFilters;

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 * 
 * https://www.playframework.com/documentation/latest/Filters
 */
@Singleton
public class HttpFilters extends DefaultHttpFilters {

//    @Inject
//    public HttpFilters(CSRFFilter csrfFilter, AllowedHostsFilter allowedHostsFilter,
//            SecurityHeadersFilter securityHeadersFilter, GzipFilter gzipFilter) {
//        super(csrfFilter, allowedHostsFilter, securityHeadersFilter, gzipFilter.asJava());
//    }

}
