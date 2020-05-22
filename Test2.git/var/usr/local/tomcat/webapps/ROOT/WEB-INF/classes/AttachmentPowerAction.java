package nccloud.web.platform.attachment.action;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import nc.bs.pub.filesystem.IFileSystemService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.vo.pub.BusinessException;
import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.core.json.IJson;
import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nccloud.pubitf.platform.attachment.tool.BillVO;

public class AttachmentPowerAction implements ICommonAction{

  @Override
  public Object doAction(IRequest request) {
    String str = request.read();
    IJson json = JsonFactory.create();
    BillVO para = json.fromJson(str, BillVO.class);
    String billId=para.getBillId();
    String billType=para.getBillType();
    IPFWorkflowQry iPFWorkflowQry =
        ServiceLocator.find(IPFWorkflowQry.class);
    boolean power=false;
    try {
      power= iPFWorkflowQry.flowIsNoStatus(billId);
    }
    catch (BusinessException ex) {
      ExceptionUtils.wrapException(ex);
    }
    return power;
  }

}
