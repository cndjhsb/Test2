package nccloud.framework.web.ui.tool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nc.vo.ml.LanguageVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.platform.appsystemplate.FormPropertyVO;
import nc.vo.platform.appsystemplate.QueryPropertyVO;
import nc.vo.pub.lang.UFBoolean;
import nccloud.commons.lang.StringUtils;
import nccloud.framework.core.model.entity.IVO;
import nccloud.framework.core.model.meta.IAttributeMeta;
import nccloud.framework.core.model.meta.IVOMeta;
import nccloud.framework.core.model.meta.ModelType;
import nccloud.framework.core.util.ToolUtil;
import nccloud.framework.service.meta.vometa.AttributeMeta;
import nccloud.framework.web.convert.translate.MetaPath;
import nccloud.framework.web.convert.translate.basedoc.BaseDocIDTranslatorFactory;
import nccloud.framework.web.convert.translate.basedoc.IBaseDocIDTranslator;
import nccloud.framework.web.ui.config.Item;
import nccloud.framework.web.ui.conver.MetaPathFinder;
import nccloud.framework.web.ui.meta.ComponentType;
import nccloud.framework.web.ui.meta.ItemOption;
import nccloud.framework.web.ui.model.row.Cell;

/**
 * 页面模板解析之 默认值解析
 * 
 * @since 2018-8-23
 * @version 1.0.0
 * @author 崔松杰
 */
public class SetInitialvalueTool {

  public void setQueryInitialvalue(Item item, QueryPropertyVO queryPropertyVO, Cell cell) {
    if (queryPropertyVO.getDefaultvalue().startsWith("#") && queryPropertyVO.getDefaultvalue().endsWith("#")) {
      cell.setValue(queryPropertyVO.getDefaultvalue());
      item.setInitialvalue(cell);
    } else if (queryPropertyVO.getItemtype().equals(ComponentType.Switch_browse.value())
        || queryPropertyVO.getItemtype().equals(ComponentType.Switch.value())
        || queryPropertyVO.getItemtype().equals(ComponentType.Checkbox_switch.value())) {
      cell.setValue(UFBoolean.valueOf(queryPropertyVO.getDefaultvalue()));
      item.setInitialvalue(cell);
    } else if (queryPropertyVO.getItemtype().equals(ComponentType.Refer.value())) {
      String[] defaultvalues = queryPropertyVO.getDefaultvalue().split("\\=");
      if (defaultvalues.length == 1) {
        cell.setDisplay(defaultvalues[0]);
      } else if (defaultvalues.length == 2) {
        String mdProperty = queryPropertyVO.getMetadataproperty();
        if (!StringUtils.isEmpty(mdProperty)) {
          Set<String> nameset = new HashSet<String>();
          Set<String> idset = new HashSet<String>();
          String vometaStr = mdProperty.substring(0, ToolUtil.getCharacterPosition(mdProperty, 2, "\\."));
          String metaPathStr = mdProperty.substring(vometaStr.length() + 1, mdProperty.length());
          MetaPath metapath = new MetaPath(vometaStr, metaPathStr);
          IVOMeta vometa = metapath.getCurrentVOMeta();
          String nameStr = metapath.getCurrentAttribute().getName();
          ModelType modType = metapath.getCurrentAttribute().getModelType();
          if (ModelType.MultiLangTextType.equals(modType)) {
            String str = metapath.getCurrentAttribute().getName();
            LanguageVO lvo = MultiLangContext.getInstance().getCurrentLangVO();
            if (lvo.getLangseq().intValue() == 1) {
              nameset.add(nameStr);
            } else {
              nameset.add(nameStr);

              nameStr = str + lvo.getLangseq().toString();
              nameset.add(nameStr);
            }
            idset.add(defaultvalues[1]);
            nameset.add(vometa.getPrimaryAttribute().getName());
            IBaseDocIDTranslator bdtranslator = BaseDocIDTranslatorFactory.getInstance()
                .getTranslator(vometa);
            IVO[] vos = bdtranslator.queryBaseDoc(vometa, idset.toArray(new String[0]),
                nameset.toArray(new String[0]));
            if (vos != null && vos.length > 0) {
              String display = (String) vos[0].getAttributeValue(nameStr);
              cell.setDisplay(display);
            }
          }
        }else {
          cell.setDisplay(defaultvalues[0]);
        }
        cell.setValue(defaultvalues[1]);
      } else {
        cell.setDisplay(defaultvalues[0]);
      }
      item.setInitialvalue(cell);
    } else if (queryPropertyVO.getItemtype().equals(ComponentType.Select.value())) {
      Map<String, String> map = new HashMap<>();
      StringBuffer display = new StringBuffer();
      StringBuffer value = new StringBuffer();
      ItemOption[] options = item.getOptions();
      String[] defalues = queryPropertyVO.getDefaultvalue().split("\\,");
      for (int i = 0; i < options.length; i++) {
        map.put(options[i].getValue(), options[i].getDisplay());
      }
      for (int j = 0; j < defalues.length; j++) {
        display.append(map.get(defalues[j]) + ",");
        value.append(defalues[j] + ",");
      }
      cell.setDisplay(display.deleteCharAt(display.length() - 1).toString());
      cell.setValue(value.deleteCharAt(value.length() - 1).toString());
      item.setInitialvalue(cell);
    } else if (queryPropertyVO.getItemtype().equals(ComponentType.Checkbox.value())) {
      StringBuffer display = new StringBuffer();
      StringBuffer value = new StringBuffer();
      Map<String, String> map = new HashMap<>();
      if (this.isNotEmpty(queryPropertyVO.getDataval())) {
      String[] s = queryPropertyVO.getDataval().split("\\,");
      String[] defaults = queryPropertyVO.getDefaultvalue().split("\\,");
      int len = defaults.length;
      for (int i = 0; i < s.length; i++) {
        String[] checkvalue = s[i].split("\\=");
        map.put(checkvalue[1].trim(), checkvalue[0].trim());
      }
      if (!map.isEmpty()) {
        for (int i = 0; i < len; i++) {
          if (map.get(defaults[i].trim()) != null) {
            display.append(map.get(defaults[i].trim()) + ",");
            value.append(defaults[i].trim() + ",");
          }
        }
        if(display.length()>0 && value.length()>0) {
          cell.setDisplay(display.deleteCharAt(display.length() - 1).toString());
          cell.setValue(value.deleteCharAt(value.length() - 1).toString());
          item.setInitialvalue(cell);
        }
      }
    }else {
      if(queryPropertyVO.getDefaultvalue().equals("Y")||queryPropertyVO.getDefaultvalue().equals("是")||queryPropertyVO.getDefaultvalue().equals("true")) {
        Boolean checkBoolean=true;
        cell.setValue(checkBoolean);
        item.setInitialvalue(cell);
      }else if(queryPropertyVO.getDefaultvalue().equals("N")||queryPropertyVO.getDefaultvalue().equals("否")||queryPropertyVO.getDefaultvalue().equals("false")){
          Boolean checkBoolean=false;
          cell.setValue(checkBoolean);
          item.setInitialvalue(cell);
      }      
    }
    } else {
      cell.setDisplay(queryPropertyVO.getDefaultvalue());
      cell.setValue(queryPropertyVO.getDefaultvalue());
      item.setInitialvalue(cell);
    }
  }

  public void setDataPowerOperationCode(String metapath, Item item) {
    MetaPathFinder finder = new MetaPathFinder(metapath, item.getCode());
    IAttributeMeta attr = finder.getLastValidAttribute();
    if (attr != null) {
      if (attr instanceof AttributeMeta) {
        AttributeMeta meta = (AttributeMeta) attr;
        item.setDataPowerOperationCode(meta.getEntity().getDataPowerOperationCode());
      }
    }
  }

  public void setFormInitialvalue(Item item, FormPropertyVO formPropertyVO, Cell cell) {
    if (formPropertyVO.getDefaultvalue().startsWith("#") && formPropertyVO.getDefaultvalue().endsWith("#")) {
      cell.setValue(formPropertyVO.getDefaultvalue());
      item.setInitialvalue(cell);
    } else if (formPropertyVO.getItemtype().equals(ComponentType.Switch_browse.value())
        || formPropertyVO.getItemtype().equals(ComponentType.Switch.value())
        || formPropertyVO.getItemtype().equals(ComponentType.Checkbox_switch.value())) {
      cell.setValue(UFBoolean.valueOf(formPropertyVO.getDefaultvalue()));
      item.setInitialvalue(cell);
    } // 解析 默认参照值 类型
    else if (formPropertyVO.getItemtype().equals(ComponentType.Refer.value())) {
      String[] defaultvalues = formPropertyVO.getDefaultvalue().split("\\=");
      if (defaultvalues.length == 1) {
        cell.setDisplay(defaultvalues[0]);
      } else if (defaultvalues.length == 2) {
        cell.setDisplay(defaultvalues[0]);
        cell.setValue(defaultvalues[1]);
      }
      item.setInitialvalue(cell);
    } else if (formPropertyVO.getItemtype().equals(ComponentType.Select.value())) {
      Map<String, String> map = new HashMap<>();
      StringBuffer display = new StringBuffer();
      StringBuffer value = new StringBuffer();
      ItemOption[] options = item.getOptions();
      String[] defalues = formPropertyVO.getDefaultvalue().split("\\,");
      for (int i = 0; i < options.length; i++) {
        map.put(options[i].getValue(), options[i].getDisplay());
      }
      for (int j = 0; j < defalues.length; j++) {
        display.append(map.get(defalues[j]) + ",");
        value.append(defalues[j] + ",");
      }
      cell.setDisplay(display.deleteCharAt(display.length() - 1).toString());
      cell.setValue(value.deleteCharAt(value.length() - 1).toString());
      item.setInitialvalue(cell);
    } else if (formPropertyVO.getItemtype().equals(ComponentType.Checkbox.value())) {
      StringBuffer display = new StringBuffer();
      StringBuffer value = new StringBuffer();
      Map<String, String> map = new HashMap<>();
      if (this.isNotEmpty(formPropertyVO.getDataval())) {
      String[] s = formPropertyVO.getDataval().split("\\,");
      String[] defaults = formPropertyVO.getDefaultvalue().split("\\,");
      int len = defaults.length;
      for (int i = 0; i < s.length; i++) {
        String[] checkvalue = s[i].split("\\=");
        map.put(checkvalue[1], checkvalue[0]);
      }
      if (!map.isEmpty()) {
        for (int i = 0; i < len; i++) {
          if (map.get(defaults[i]) != null) {
            display.append(map.get(defaults[i]) + ",");
            value.append(defaults[i] + ",");
          }
        }
        cell.setDisplay(display.deleteCharAt(display.length() - 1).toString());
        cell.setValue(value.deleteCharAt(value.length() - 1).toString());
        item.setInitialvalue(cell);
      }
      }else {
        if(formPropertyVO.getDefaultvalue().equals("Y")||formPropertyVO.getDefaultvalue().equals("是")||formPropertyVO.getDefaultvalue().equals("true")) {
          Boolean checkBoolean=true;
          cell.setValue(checkBoolean);
          cell.setDisplay("是");
          item.setInitialvalue(cell);
        }else if(formPropertyVO.getDefaultvalue().equals("N")||formPropertyVO.getDefaultvalue().equals("否")||formPropertyVO.getDefaultvalue().equals("false")){
            Boolean checkBoolean=false;
            cell.setValue(checkBoolean);
            cell.setDisplay("否");
            item.setInitialvalue(cell);
        }      
      }
    } else {
      cell.setDisplay(formPropertyVO.getDefaultvalue());
      cell.setValue(formPropertyVO.getDefaultvalue());
      item.setInitialvalue(cell);
    }
  }
  
  private boolean isNotEmpty(String str) {
    return (str != null) && (str.trim().length() > 0);
  }
}
