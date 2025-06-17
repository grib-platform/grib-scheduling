package kr.co.grib.scheduling.service.client;

import java.util.List;

import kr.co.grib.scheduling.dto.ClientDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;

public interface ClientService {
    public ResponseDto<Void> createClient(ClientDto param);
    
    public ResponseDto<List<ClientDto>> readClientList(int pageNumber, int pageSize, String orderBy, String order, ClientDto param);
    
    public ResponseDto<Void> updateClient(ClientDto param);
    
    public ResponseDto<Void> deleteClient(ClientDto param);
}