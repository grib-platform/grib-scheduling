package kr.co.grib.scheduling.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.grib.scheduling.dto.ClientDto;
import kr.co.grib.scheduling.dto.common.ResponseDto;
import kr.co.grib.scheduling.service.client.ClientService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService clientService;
    
    @PostMapping("/create")
    public ResponseEntity<ResponseDto<Void>> createClient(HttpServletRequest request, @RequestBody ClientDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = clientService.createClient(param);
            if(result.getCode().equals("FAIL")){
                return ResponseEntity.badRequest().body(result);
            }else{
                return ResponseEntity.ok(result);
            }
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @GetMapping("/read")
    public ResponseEntity<ResponseDto<List<ClientDto>>> readClientList(
        HttpServletRequest request, 
        @RequestParam("pageNumber") int pageNumber,
        @RequestParam("pageSize") int pageSize,
        @RequestParam("orderBy") String orderBy,
        @RequestParam("order") String order,
        ClientDto param
    ) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<List<ClientDto>> result = clientService.readClientList(pageNumber, pageSize, orderBy, order, param);
            if(result.getCode().equals("FAIL")){
                return ResponseEntity.badRequest().body(result);
            }else{
                return ResponseEntity.ok(result);
            }
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto<Void>> updateClient(HttpServletRequest request, @RequestBody ClientDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = clientService.updateClient(param);
            if(result.getCode().equals("FAIL")){
                return ResponseEntity.badRequest().body(result);
            }else{
                return ResponseEntity.ok(result);
            }
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<Void>> deleteClient(HttpServletRequest request, @RequestBody ClientDto param) {
        String apiStatus = (String) request.getAttribute("apiStatus");
        if(apiStatus.equals("NORMAL")){
            ResponseDto<Void> result = clientService.deleteClient(param);
            if(result.getCode().equals("FAIL")){
                return ResponseEntity.badRequest().body(result);
            }else{
                return ResponseEntity.ok(result);
            }
        }else{
            return ResponseEntity.ok(ResponseDto.error("FAIL", apiStatus, null));
        }
    }
}