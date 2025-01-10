package com.demo.shopapp.entities;

import jakarta.persistence.*;
import lombok.*;



@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
       return this.name;
    }

    // Private constructor để chỉ có thể tạo đối tượng thông qua Builder
    private Category(String name) {
        this.name = name;
    }

    // Builder class
    public static class CategoryBuilder {
        private Long id;
        private String name;

        // Phương thức để gán tên cho category
        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        // Phương thức để gán id cho category
        public CategoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        // Phương thức build để trả về đối tượng Category
        public Category build() {
            return new Category(name);
        }
    }

    // Phương thức để lấy đối tượng builder
    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

}



//CREATE TABLE [categories] (
//        [id] BIGINT PRIMARY KEY IDENTITY(1, 1),
//  [name] varchar(100) NOT NULL DEFAULT ''
//        )
//GO