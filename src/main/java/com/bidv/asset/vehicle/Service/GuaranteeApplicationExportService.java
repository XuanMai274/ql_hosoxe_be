package com.bidv.asset.vehicle.Service;

public interface GuaranteeApplicationExportService {
    byte[] exportDeNghiCapBaoLanh(Long applicationId) throws Exception;

    byte[] exportDanhSachXeBaoLanh(Long applicationId) throws Exception;
}
