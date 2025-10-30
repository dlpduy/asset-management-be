package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.asset.AssetRequest;
import com.example.demo.dto.asset.AssetResponse;
import com.example.demo.entity.Asset;
import com.example.demo.entity.AssetType;
import com.example.demo.entity.Department;
import com.example.demo.entity.User;
import com.example.demo.exception.DataNotFound;
import com.example.demo.repository.AssetRepository;
import com.example.demo.repository.AssetTypeRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetService {
    private final AssetRepository assetRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public void create(AssetRequest assetRequest) {
        AssetType type = assetTypeRepository.findById(assetRequest.getTypeId())
                .orElseThrow(() -> new DataNotFound("Asset type not found"));

        User user = userRepository.findById(assetRequest.getAssignedTo())
                .orElseThrow(() -> new DataNotFound("User not found"));

        Asset asset = Asset.builder().code(assetRequest.getCode())
                .name(assetRequest.getName())
                .type(type)
                .assignedTo(user)
                .purchaseDate(assetRequest.getPurchaseDate())
                .value(assetRequest.getValue())
                .status(assetRequest.getStatus())
                .condition(assetRequest.getCondition())
                .description(assetRequest.getDescription())
                .build();
        assetRepository.save(asset);
    }

    public void update(Long id, AssetRequest assetRequest) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new DataNotFound("Asset not found"));

        AssetType type = assetTypeRepository.findById(assetRequest.getTypeId())
                .orElseThrow(() -> new DataNotFound("Asset type not found"));

        User user = userRepository.findById(assetRequest.getAssignedTo())
                .orElseThrow(() -> new DataNotFound("User not found"));

        asset.setCode(assetRequest.getCode());
        asset.setName(assetRequest.getName());
        asset.setType(type);
        asset.setAssignedTo(user);
        asset.setPurchaseDate(assetRequest.getPurchaseDate());
        asset.setValue(assetRequest.getValue());
        asset.setStatus(assetRequest.getStatus());
        asset.setCondition(assetRequest.getCondition());
        asset.setDescription(assetRequest.getDescription());

        assetRepository.save(asset);
    }

    public void delete(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new DataNotFound("Asset not found"));
        assetRepository.delete(asset);
    }

    public AssetResponse getById(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new DataNotFound("Asset not found"));

        AssetType type = assetTypeRepository.findById(id)
                .orElseThrow(() -> new DataNotFound("Asset type not found"));

        return AssetResponse.builder()
                .id(asset.getId())
                .code(asset.getCode())
                .name(asset.getName())
                .type(type.getName())
                .assignedTo(asset.getAssignedTo() == null ? null : asset.getAssignedTo().getName())
                .purchaseDate(asset.getPurchaseDate())
                .value(asset.getValue())
                .status(asset.getStatus().name())
                .condition(asset.getCondition().name())
                .description(asset.getDescription())
                .createdBy(asset.getCreatedBy().getName())
                .createdAt(asset.getCreatedAt())
                .build();
    }

    public List<AssetResponse> getAll() {
        List<Asset> assets = assetRepository.findAll();
        return assets.stream().map(asset -> AssetResponse.builder()
                .id(asset.getId())
                .code(asset.getCode())
                .name(asset.getName())
                .type(asset.getType().getName())
                .assignedTo(asset.getAssignedTo() == null ? null : asset.getAssignedTo().getName())
                .purchaseDate(asset.getPurchaseDate())
                .value(asset.getValue())
                .status(asset.getStatus().name())
                .condition(asset.getCondition().name())
                .description(asset.getDescription())
                .createdBy(asset.getCreatedBy().getName())
                .createdAt(asset.getCreatedAt())
                .build()).toList();
    }
}
