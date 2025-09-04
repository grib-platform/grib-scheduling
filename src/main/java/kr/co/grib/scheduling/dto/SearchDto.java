package kr.co.grib.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchDto {
    @Builder.Default
    private String periodColumns = "";

    @Builder.Default
    private String periodFrom = "";

    @Builder.Default
    private String periodTo = "";

    @Builder.Default
    private String keywordColumn = "";

    @Builder.Default
    private String keyword = "";
}
