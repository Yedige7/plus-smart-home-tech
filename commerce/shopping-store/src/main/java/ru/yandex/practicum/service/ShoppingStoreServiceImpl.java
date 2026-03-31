package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.PageResponseDto;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SortDto;
import ru.yandex.practicum.entity.ProductEntity;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ShoppingStoreServiceImpl(ProductRepository productRepository,
                                    ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {
        ProductEntity entity = productMapper.toEntity(productDto);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto update(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        if (productId == null) {
            throw new ProductNotFoundException("Идентификатор товара не указан");
        }

        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден: " + productId));

        productMapper.update(entity, productDto);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toDto(saved);
    }

    @Override
    public ProductDto getById(UUID productId) {
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден: " + productId));

        return productMapper.toDto(entity);
    }

    public PageResponseDto<ProductDto> getProducts(ProductCategory category, int page, int size, List<String> sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<ProductEntity> entityPage = productRepository.findAllByCategoryAndState(
                category,
                ProductState.ACTIVE,
                pageable
        );

        List<ProductDto> content = entityPage.getContent()
                .stream()
                .map(productMapper::toDto)
                .toList();

        List<SortDto> sortDtos = pageable.getSort().stream()
                .map(order -> new SortDto(
                        order.getDirection().name(),
                        mapSortProperty(order.getProperty()),
                        order.isAscending(),
                        order.isIgnoreCase(),
                        order.getNullHandling().name()
                ))
                .toList();

        PageableDto pageableDto = new PageableDto(
                pageable.getOffset(),
                sortDtos,
                pageable.isUnpaged(),
                pageable.isPaged(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return new PageResponseDto<>(
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isFirst(),
                entityPage.isLast(),
                entityPage.getSize(),
                content,
                entityPage.getNumber(),
                sortDtos,
                entityPage.getNumberOfElements(),
                pageableDto,
                entityPage.isEmpty()
        );
    }

    private String mapSortProperty(String property) {
        return switch (property) {
            case "productId" -> "id";
            case "productCategory" -> "category";
            case "productState" -> "state";
            default -> property;
        };
    }

    private Sort parseSort(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();

        for (int i = 0; i < sortParams.size(); i++) {
            String current = sortParams.get(i);
            if (current == null || current.isBlank()) {
                continue;
            }

            String property;
            Sort.Direction direction = Sort.Direction.ASC;

            if (current.contains(",")) {
                String[] parts = current.split(",", 2);
                property = parts[0].trim();
                if (parts.length > 1 && !parts[1].isBlank()) {
                    direction = Sort.Direction.fromString(parts[1].trim());
                }
            } else {
                property = current.trim();

                if (i + 1 < sortParams.size()) {
                    String next = sortParams.get(i + 1);
                    if (next != null && ("ASC".equalsIgnoreCase(next) || "DESC".equalsIgnoreCase(next))) {
                        direction = Sort.Direction.fromString(next.trim());
                        i++;
                    }
                }
            }

            orders.add(new Sort.Order(direction, mapSortProperty(property)));
        }

        return orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
    }


    @Override
    @Transactional
    public boolean remove(UUID productId) {
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден: " + productId));

        entity.setState(ProductState.DEACTIVATE);
        productRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден: " + productId));

        entity.setQuantityState(quantityState);
        productRepository.save(entity);
        return true;
    }

}