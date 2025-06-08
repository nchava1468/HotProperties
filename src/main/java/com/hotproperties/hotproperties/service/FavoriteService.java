package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Favorite;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FavoriteService {

    @PreAuthorize("hasRole('BUYER')")
    List<Favorite> getFavorites(User user);

    boolean favoriteExists(Property property, User user);

    @PreAuthorize("hasRole('BUYER')")
    void addFavorite(Property property, User user);

    @PreAuthorize("hasRole('BUYER')")
    void deleteFavorite(Property property, User user);
}
