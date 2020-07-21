package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.config.ExtOrder;
import cn.willingxyz.restdoc.core.models.ControllerModel;
import cn.willingxyz.restdoc.core.parse.IControllerParser;
import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.google.auto.service.AutoService;

import java.util.Collections;
import java.util.Optional;

@AutoService(IControllerParser.class)
@ExtOrder(Integer.MIN_VALUE)
public class JavadocControllerParser implements IControllerParser {

    @Override
    public void parse(Class clazz, ClassJavadoc classDoc, ControllerModel controllerModel) {
        controllerModel.setControllerClass(clazz);
        if (classDoc != null && !classDoc.isEmpty()) {
            controllerModel.setDescription(FormatUtils.format(classDoc.getComment()));
            String tag = Optional.ofNullable(classDoc.getOther())
                .orElse(Collections.emptyList())
                .stream()
                .filter(doc -> doc.getName().equals("tag"))
                .map(doc -> FormatUtils.format(doc.getComment()))
                .findAny()
                .orElse(null);

            controllerModel.setTag(tag);
        }
    }
}
