package io.github.wulkanowy.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "Accounts")
public class Account {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "NAME")
    private String name;

    @Property(nameInDb = "E-MAIL")
    private String email;

    @Property(nameInDb = "PASSWORD")
    private String password;

    @Property(nameInDb = "SYMBOL")
    private String symbol;

    @Generated(hash = 1514643300)
    public Account(Long id, String name, String email, String password,
                   String symbol) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Account setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Account setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Account setSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }
}
