package kr.co.grib.scheduling.service.client.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.grib.scheduling.domain.Client;
import kr.co.grib.scheduling.dto.ClientDto;
import kr.co.grib.scheduling.dto.common.PageResponse;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.repository.ClientRepository;
import kr.co.grib.scheduling.service.client.ClientService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    @Override
    public ResponseDto<Void> createClient(ClientDto param) {
        try{
            Client client = Client.builder()
                            .clientId(param.getClientId())
                            .accessToken(param.getAccessToken())
                            .refreshToken(param.getRefreshToken())
                            .callbackUrl(param.getCallbackUrl())
                            .build();
            clientRepository.save(client);
            return ResponseDto.data(null);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public ResponseDto<List<ClientDto>> readClientList(int pageNumber, int pageSize, String orderBy, String order, ClientDto param) {
        try{
            Sort sortClient = Sort.by(orderBy);
            if(order.equals("desc")){
                sortClient = sortClient.descending();
            }else{
                sortClient = sortClient.ascending();
            }
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sortClient);
            List<ClientDto> clientDtoList = null;
            Page<Client> clientList = null;
            if(param.getClientId() != null){
                clientList = clientRepository.findAllByClientIdContains(pageable, param.getClientId());
                clientDtoList = clientList.stream().map(client -> {
                    return new ClientDto(client);
                }).toList();
            }else if(param.getCallbackUrl() != null){
                clientList = clientRepository.findAllByCallbackUrlContains(pageable, param.getCallbackUrl());
                clientDtoList = clientList.stream().map(client -> {
                    return new ClientDto(client);
                }).toList();
            }else{
                clientList = clientRepository.findAll(pageable);
                clientDtoList = clientList.stream().map(client -> {
                    return new ClientDto(client);
                }).toList();
            }
            return ResponseDto.data(clientDtoList, PageResponse.of(pageNumber, pageSize, clientList.getTotalElements()));
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public ResponseDto<Void> updateClient(ClientDto param) {
        try{
            Optional<Client> client = clientRepository.findById(param.getClientId());
            Client clientObj = client.get();
            if(param.getCallbackUrl() != null){
                clientObj.setCallbackUrl(param.getCallbackUrl());
            }
            clientRepository.save(clientObj);
            return ResponseDto.data(null);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public ResponseDto<Void> deleteClient(ClientDto param) {
        try{
            Optional<Client> client = clientRepository.findById(param.getClientId());
            clientRepository.delete(client.get());
            return ResponseDto.data(null);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
