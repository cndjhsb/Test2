package nccloud.web.platform.fixdata;

import java.util.HashMap;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nccloud.framework.core.json.IJson;
import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.pubitf.platform.datafix.DataFixService;

public class BackFixDataAction implements ICommonAction{

	public Object doAction(IRequest request) {
	  IJson json = JsonFactory.create();
    String requestString = request.read();
    Map params = (Map) json.fromJson(requestString, HashMap.class);
    String gtxid = (String) params.get("gtxid");
		DataFixService service = ServiceLocator.find(DataFixService.class);	// cate module
//		try {
//			service.compensate(gtxid);
//		} catch (BusinessException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		}
		return null;
	}
}