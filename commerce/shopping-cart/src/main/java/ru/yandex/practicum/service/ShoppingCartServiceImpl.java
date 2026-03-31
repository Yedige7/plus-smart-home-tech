package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.entity.ShoppingCartEntity;
import ru.yandex.practicum.entity.ShoppingCartItemEntity;
import ru.yandex.practicum.enums.ShoppingCartState;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repository.ShoppingCartItemRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository,
                                   ShoppingCartItemRepository shoppingCartItemRepository,
                                   ShoppingCartMapper shoppingCartMapper,
                                   WarehouseClient warehouseClient) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.shoppingCartMapper = shoppingCartMapper;
        this.warehouseClient = warehouseClient;
    }

    @Override
    public ShoppingCartDto getCart(String username) {
        validateUsername(username);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> createCart(username));

        List<ShoppingCartItemEntity> items = shoppingCartItemRepository.findAllByCartId(cart.getId());
        return shoppingCartMapper.toDto(cart, items);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        validateUsername(username);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> createCart(username));

        checkCartActive(cart);

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            Map<UUID, Long> singleProductMap = new HashMap<>();
            singleProductMap.put(productId, quantity);

            ShoppingCartDto cartDto = new ShoppingCartDto(cart.getId(), singleProductMap);

            BookedProductsDto ignored = warehouseClient.checkProducts(cartDto);

            ShoppingCartItemEntity item = shoppingCartItemRepository
                    .findByCartIdAndProductId(cart.getId(), productId)
                    .orElseGet(() -> {
                        ShoppingCartItemEntity newItem = new ShoppingCartItemEntity();
                        newItem.setCart(cart);
                        newItem.setProductId(productId);
                        newItem.setQuantity(0);
                        return newItem;
                    });

            item.setQuantity(item.getQuantity() + quantity);
            shoppingCartItemRepository.save(item);
        }

        return shoppingCartMapper.toDto(cart, shoppingCartItemRepository.findAllByCartId(cart.getId()));
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> createCart(username));

        checkCartActive(cart);

        ShoppingCartItemEntity item = shoppingCartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseThrow(() -> new NoProductsInShoppingCartException("Товар отсутствует в корзине"));

        Map<UUID, Long> singleProductMap = new HashMap<>();
        singleProductMap.put(request.getProductId(), request.getNewQuantity());

        ShoppingCartDto cartDto = new ShoppingCartDto(cart.getId(), singleProductMap);

        // если товара не хватает, warehouse должен выбросить ошибку
        BookedProductsDto ignored = warehouseClient.checkProducts(cartDto);

        item.setQuantity(request.getNewQuantity());
        shoppingCartItemRepository.save(item);

        return shoppingCartMapper.toDto(cart,
                shoppingCartItemRepository.findAllByCartId(cart.getId()));
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> productIds) {
        validateUsername(username);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Корзина не найдена"));

        checkCartActive(cart);

        List<ShoppingCartItemEntity> items = shoppingCartItemRepository.findAllByCartId(cart.getId());

        boolean removedAny = false;

        for (UUID productId : productIds) {
            for (ShoppingCartItemEntity item : items) {
                if (item.getProductId().equals(productId)) {
                    shoppingCartItemRepository.delete(item);
                    removedAny = true;
                }
            }
        }

        if (!removedAny) {
            throw new NoProductsInShoppingCartException("Нет таких товаров в корзине");
        }

        return shoppingCartMapper.toDto(cart,
                shoppingCartItemRepository.findAllByCartId(cart.getId()));
    }

    @Override
    @Transactional
    public void deactivate(String username) {
        validateUsername(username);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Корзина не найдена: " + username));

        cart.setState(ShoppingCartState.DEACTIVATE);
        shoppingCartRepository.save(cart);
    }

    private ShoppingCartEntity createCart(String username) {
        ShoppingCartEntity cart = new ShoppingCartEntity();
        cart.setUsername(username);
        cart.setState(ShoppingCartState.ACTIVE);
        return shoppingCartRepository.save(cart);
    }

    private void checkCartActive(ShoppingCartEntity cart) {
        if (cart.getState() == ShoppingCartState.DEACTIVATE) {
            throw new ShoppingCartDeactivatedException("Корзина деактивирована");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}