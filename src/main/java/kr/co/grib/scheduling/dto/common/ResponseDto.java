package kr.co.grib.scheduling.dto.common;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ResponseDto<T> {

    private String code;
    private String message;
    private T data;
    private PageResponse pagination;

    @Builder
    public ResponseDto(String code, String message, T data, PageResponse page) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.pagination = page;

    }

    public ResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseDto<T> data(@Nullable T data) {
        return new ResponseDto<>(HttpStatus.OK.getReasonPhrase(), "", data, null);
    }

    public static <T> ResponseDto<T> data(@Nullable T data, PageResponse pagination) {
        return new ResponseDto<>(HttpStatus.OK.getReasonPhrase(), "", data, pagination);
    }

    public static <T> ResponseDto<T> error(String code, String message, @Nullable T data) {
        return new ResponseDto<>(code, message, data);
    }

    public static <T> ResponseDto<T> code(String code) {
        return new ResponseDto<>(code, "", null, null);
    }
}