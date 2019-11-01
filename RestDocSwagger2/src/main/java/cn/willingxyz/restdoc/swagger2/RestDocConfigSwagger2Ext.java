package cn.willingxyz.restdoc.swagger2;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RestDocConfigSwagger2Ext {
    private List<ISwaggerFilter> _swaggerFilters = new ArrayList<>();
}
