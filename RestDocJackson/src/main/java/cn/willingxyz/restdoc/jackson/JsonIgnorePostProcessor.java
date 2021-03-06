package cn.willingxyz.restdoc.jackson;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.auto.service.AutoService;

/**
 * com.fasterxml.jackson.annotation.JsonIgnore
 */
@AutoService(IPropertyPostProcessor.class)
public class JsonIgnorePostProcessor implements IPropertyPostProcessor {
    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        JsonIgnore jsonIgnoreAnno = propertyModel.getPropertyItem().getAnnotation(JsonIgnore.class);
        if (jsonIgnoreAnno == null)
            return propertyModel;

        if (jsonIgnoreAnno.value())
        {
            return null;
        }
        return propertyModel;
    }
}
