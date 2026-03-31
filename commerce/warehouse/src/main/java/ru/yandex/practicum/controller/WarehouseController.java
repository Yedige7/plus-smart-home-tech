package ru.yandex.practicum.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.WarehouseApi;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@RestController
public class WarehouseController implements WarehouseApi {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        warehouseService.newProductInWarehouse(request);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto cart) {
        return warehouseService.checkProducts(cart);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}