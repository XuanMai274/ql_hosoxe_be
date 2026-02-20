package com.bidv.asset.vehicle.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportDeXuatRequest {

    private GuaranteeLetterDTO guaranteeLetter;
    private XuatDeXuatBaoLanh exportData;
}