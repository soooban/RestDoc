package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.PropertyModel;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 解析Type为 PropertyModel 列表
 */
public interface ITypeParser {
    List<PropertyModel> parse(Type type);
}
