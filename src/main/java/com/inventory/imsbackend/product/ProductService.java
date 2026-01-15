package com.inventory.imsbackend.product;

import com.inventory.imsbackend.exception.ResourceNotFoundException;
import com.inventory.imsbackend.product.dto.ProductCreateRequest;
import com.inventory.imsbackend.product.dto.ProductUpdateRequest;
import com.inventory.imsbackend.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

// paggination and sorting
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.inventory.imsbackend.product.dto.PaginatedProductResponse;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // CREATE
    public ProductResponse createProduct(ProductCreateRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("SKU already exists");
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .category(request.getCategory())
                .costPrice(request.getCostPrice())
                .sellingPrice(request.getSellingPrice())
                .quantity(request.getQuantity())
                .status(ProductStatus.ACTIVE)
                .build();

        return mapToResponse(productRepository.save(product));
    }

    // GET ALL ACTIVE
//    public List<ProductResponse> getAllActiveProducts() {
//        return productRepository.findByStatus(ProductStatus.ACTIVE)
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//    Re written getAllACtiveProduct to accept paramenters and return new DTO
    public PaginatedProductResponse getAllActiveProducts(int pageNo, int pageSize, String sortBy, String sortDir){

        // Decide sort direction
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create pageable object
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Fetch paginated results from repo
        Page<Product> productPage = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);

        // map content to DTOs
        List<ProductResponse> concent = productPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        // use the Builder from PaginatedProductResponse
        return PaginatedProductResponse.builder()
                .content(concent)
                .pageNo(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }


    // GET BY ID
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        return mapToResponse(product);
    }

    // UPDATE
    public ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );
        if (product.getStatus() == ProductStatus.INACTIVE){
            throw new IllegalArgumentException("cannot update a deactivated product. Reactivate it first if needed.");
        }
        // SKU NEVER changes
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setCostPrice(request.getCostPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setQuantity(request.getQuantity());
        product.setStatus(request.getStatus());

        return mapToResponse(productRepository.save(product));
    }

    // SEARCH Product
    public PaginatedProductResponse searchProduct(
            String name, String category, int pageNo, int pageSize, String sortBy, String sortDir){

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Product> productsPage;

        // fetch filtered data

        //handle option category filtering
        if (category == null || category.trim().isEmpty()){
            //search by name
            productsPage = productRepository.findByNameContainingIgnoreCaseAndStatus(
                    name, ProductStatus.ACTIVE, pageable);
        } else {
            // search by both Name and Category
            productsPage = productRepository.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCaseAndStatus(
                    name, category, ProductStatus.ACTIVE, pageable);
        }

        List<ProductResponse> content = productsPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PaginatedProductResponse.builder()
                .content(content)
                .pageNo(productsPage.getNumber())
                .pageSize(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .last(productsPage.isLast())
                .build();
    }

    // SOFT DELETE
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found")
                );

        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    // MAPPER
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setCategory(product.getCategory());
        response.setSellingPrice(product.getSellingPrice());
        response.setQuantity(product.getQuantity());
        response.setStatus(product.getStatus());
        return response;
    }
}
