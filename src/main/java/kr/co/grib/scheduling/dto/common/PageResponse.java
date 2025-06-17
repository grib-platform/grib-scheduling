package kr.co.grib.scheduling.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse {

    private Integer page;
    private Integer size;
    private Long total;

    public static PageResponse of(Integer page, Integer size, Long total){
        return new PageResponse(page, size, total);
    }
}
