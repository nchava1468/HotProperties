package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Favorite;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;

import java.util.List;

public interface FavoriteService {

    List<Favorite> getFavorites(User user);

    boolean favoriteExists(Property property, User user);

    void addFavorite(Property property, User user);

    void deleteFavorite(Property property, User user);
}
