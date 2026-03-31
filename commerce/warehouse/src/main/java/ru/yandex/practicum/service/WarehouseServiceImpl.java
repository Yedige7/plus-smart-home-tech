package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.WarehouseProductEntity;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseMapper warehouseMapper;
    private final WarehouseAddressService warehouseAddressService;

    public WarehouseServiceImpl(WarehouseProductRepository warehouseProductRepository,
                                WarehouseMapper warehouseMapper,
                                WarehouseAddressService warehouseAddressService) {
        this.warehouseProductRepository = warehouseProductRepository;
        this.warehouseMapper = warehouseMapper;
        this.warehouseAddressService = warehouseAddressService;
    }

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseProductRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар уже есть на складе");
        }

        WarehouseProductEntity entity = warehouseMapper.toEntity(request);
        warehouseProductRepository.save(entity);
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProductEntity entity = warehouseProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе"));

        entity.setQuantity(entity.getQuantity() + request.getQuantity());
        warehouseProductRepository.save(entity);
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCartDto) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : shoppingCartDto.getProducts().entrySet()) {
            WarehouseProductEntity entity = warehouseProductRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ProductInShoppingCartLowQuantityInWarehouse("Товара нет на складе"));

            long requested = entry.getValue();
            if (entity.getQuantity() < requested) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе");
            }

            totalWeight += entity.getWeight() * requested;
            totalVolume += entity.getWidth() * entity.getHeight() * entity.getDepth() * requested;
            fragile = fragile || entity.isFragile();
        }

        return new BookedProductsDto(totalWeight, totalVolume, fragile);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseAddressService.getAddress();
    }
}