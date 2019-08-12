package samples.api;

import api.impl.BaseApi;
import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * API function sample.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiFuncSample extends BaseApi {
    public final static IApiHandler API_ECHO = ApiFuncSample::echo;
    public final static IApiHandler API_INFO = ApiFuncSample::info;
    public final static IApiHandler API_DENY = ApiFuncSample::deny;

    public final static IApiHandler API_LIST_EMPLOYEES = ApiFuncSample::listEmployees;
    public final static IApiHandler API_CREATE_EMPLOYEE = ApiFuncSample::createEmployee;
    public final static IApiHandler API_GET_EMPLOYEE = ApiFuncSample::getEmployee;
    public final static IApiHandler API_DELETE_EMPLOYEE = ApiFuncSample::deleteEmployee;
    public final static IApiHandler API_UPDATE_EMPLOYEE = ApiFuncSample::updateEmployee;

    /**
     * Data model: employee
     */
    private static class Employee {
        public String id, email, fullname;

        public Employee(String id, String email, String fullname) {
            this.id = id;
            this.email = email;
            this.fullname = fullname;
        }

        public Map<String, Object> toMap() {
            return new HashMap<>() {
                private static final long serialVersionUID = 1L;

                {
                    put("id", id);
                    put("email", email);
                    put("fullname", fullname);
                }
            };
        }
    }

    private final static Map<String, Employee> storage = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));

    /**
     * API: List all available employees.
     */
    public static ApiResult listEmployees(ApiContext context, ApiAuth auth, ApiParams params) {
        List<Map<String, Object>> result = new LinkedList<>();
        storage.forEach((k, v) -> result.add(v.toMap()));
        return ApiResult.resultOk(result);
    }

    /**
     * API: Create a new employee record.
     */
    public static ApiResult createEmployee(ApiContext context, ApiAuth auth, ApiParams params) {
        String email = params.getParam("email", String.class);
        if (StringUtils.isBlank(email)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [email] parameter.");
        }
        String fullname = params.getParam("fullname", String.class);
        if (StringUtils.isBlank(fullname)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [fullname] parameter.");
        }
        synchronized (storage) {
            email = email.trim().toLowerCase();
            fullname = fullname.trim();
            AtomicBoolean duplicated = new AtomicBoolean();
            String _email = email;
            storage.forEach((k, v) -> duplicated.compareAndSet(false, StringUtils.equals(v.email, _email)));
            if (duplicated.get()) {
                return new ApiResult(ApiResult.STATUS_ERROR_CLIENT,
                        "Employee with email [" + email + "] already exists.");
            }
            String id = Integer.toString(storage.size() + 1);
            Employee employee = new Employee(id, email, fullname);
            storage.put(id, employee);
            return ApiResult.resultOk(employee.toMap());
        }
    }

    /**
     * API: Get an employee record by id.
     */
    public static ApiResult getEmployee(ApiContext context, ApiAuth auth, ApiParams params) {
        String id = params.getParam("id", String.class);
        if (StringUtils.isBlank(id)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [id] parameter.");
        }
        Employee employee = storage.get(id);
        if (employee == null) {
            return ApiResult.DEFAULT_RESULT_NOT_FOUND;
        }
        return ApiResult.resultOk(employee.toMap());
    }

    /**
     * API: Update an existing employee record by id.
     */
    public static ApiResult updateEmployee(ApiContext context, ApiAuth auth, ApiParams params) {
        String id = params.getParam("id", String.class);
        if (StringUtils.isBlank(id)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [id] parameter.");
        }
        Employee employee = storage.get(id);
        if (employee == null) {
            return ApiResult.DEFAULT_RESULT_NOT_FOUND;
        }
        String email = params.getParam("email", String.class);
        if (StringUtils.isBlank(email)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [email] parameter.");
        }
        String fullname = params.getParam("fullname", String.class);
        if (StringUtils.isBlank(fullname)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [fullname] parameter.");
        }
        synchronized (storage) {
            email = email.trim().toLowerCase();
            fullname = fullname.trim();
            AtomicBoolean duplicated = new AtomicBoolean();
            String _email = email;
            String _eEmail = employee.email;
            storage.forEach((k, v) -> duplicated.compareAndSet(false, !StringUtils.equals(_eEmail, _email) && StringUtils.equals(v.email, _email)));
            if (duplicated.get()) {
                return new ApiResult(ApiResult.STATUS_ERROR_CLIENT,
                        "Employee with email [" + email + "] already exists.");
            }
            employee.email = email;
            employee.fullname = fullname;
            storage.put(id, employee);
            return ApiResult.resultOk(employee.toMap());
        }
    }

    /**
     * API: Delete an employee record by id.
     */
    public static ApiResult deleteEmployee(ApiContext context, ApiAuth auth, ApiParams params) {
        String id = params.getParam("id", String.class);
        if (StringUtils.isBlank(id)) {
            return new ApiResult(ApiResult.STATUS_ERROR_CLIENT, "Invalid value for [id] parameter.");
        }
        Employee employee = storage.remove(id);
        if (employee == null) {
            return ApiResult.DEFAULT_RESULT_NOT_FOUND;
        }
        return ApiResult.resultOk(employee.toMap());
    }

    /*----------------------------------------------------------------------*/

    public static ApiResult echo(ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        return ApiResult.resultOk(params.getAllParams());
    }

    public static ApiResult info(ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        Map<String, Object> serverInfo = MapUtils.createMap(
            "name", getRegistry().getAppConfig().getString("app.name"), 
            "version", getRegistry().getAppConfig().getString("app.version"), 
            "node", InetAddress.getLocalHost().getHostAddress());

        Map<String, Object> result = MapUtils
                .createMap("context", context.getAllContextFields(), "params", params.getAllParams(), "server",
                        serverInfo);
        return ApiResult.resultOk(result);
    }

    public static ApiResult deny(ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        return ApiResult.DEFAULT_RESULT_ACCESS_DENIED;
    }
}
