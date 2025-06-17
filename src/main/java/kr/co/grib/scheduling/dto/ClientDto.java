package kr.co.grib.scheduling.dto;

import kr.co.grib.scheduling.domain.Client;
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
public class ClientDto {
    private String clientId;
    private String accessToken;
    private String refreshToken;
    private String callbackUrl;
    
    public ClientDto(Client client){
        this.clientId = client.getClientId();
        this.accessToken = client.getAccessToken();
        this.refreshToken = client.getRefreshToken();
        this.callbackUrl = client.getCallbackUrl();
    }
}
