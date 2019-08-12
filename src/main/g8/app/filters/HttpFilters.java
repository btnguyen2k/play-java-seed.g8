package filters;

import play.api.http.EnabledFilters;
import play.filters.cors.CORSFilter;
import play.filters.csrf.CSRFFilter;
import play.filters.gzip.GzipFilter;
import play.filters.headers.SecurityHeadersFilter;
import play.filters.hosts.AllowedHostsFilter;
import play.http.DefaultHttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * This class configures filters that run on every request. This
 * class is queried by Play to get a list of filters.
 *
 * @see <a href="https://www.playframework.com/documentation/2.7.x/Filters">Play Filters</a>
 */
@Singleton
public class HttpFilters extends DefaultHttpFilters {
    @Inject
    public HttpFilters(EnabledFilters enabledFilters, CORSFilter corsFilter, CSRFFilter csrfFilter,
            AllowedHostsFilter allowedHostsFilter, SecurityHeadersFilter securityHeadersFilter, GzipFilter gzipFilter) {
        //combine enabled filters with custom filters
        super(combine(enabledFilters.asJava().getFilters(), corsFilter.asJava(), csrfFilter.asJava(),
                allowedHostsFilter.asJava(), securityHeadersFilter.asJava(), gzipFilter.asJava()));
    }

    private static List<EssentialFilter> combine(List<EssentialFilter> filters, EssentialFilter... toAppend) {
        List<EssentialFilter> combinedFilters = new ArrayList<>(filters);
        if (toAppend != null) {
            for (EssentialFilter essentialFilter : toAppend) {
                combinedFilters.add(essentialFilter);
            }
        }
        return combinedFilters;
    }
}
