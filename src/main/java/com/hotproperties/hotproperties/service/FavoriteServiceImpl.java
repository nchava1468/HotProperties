package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Favorite;
import com.hotproperties.hotproperties.entity.Property;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService{

    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public List<Favorite> getFavorites(User user) {
        return favoriteRepository.findByUser(user);
    }

    @Override
    public boolean favoriteExists(Property property, User user) {
        Favorite favorite = favoriteRepository.findByPropertyAndUser(property, user);
        return favorite != null;
    }

    @Override
    public void addFavorite(Property property, User user) {
        if (favoriteExists(property, user)) {
            return;
        } else {
            Favorite favoriteAdded = new Favorite(user, property);
            favoriteAdded.setTimeCreated(LocalDateTime.now());
            favoriteRepository.save(favoriteAdded);
        }
    }

    @Override
    public void deleteFavorite(Property property, User user) {
        Favorite deletedFavorite = favoriteRepository.findByPropertyAndUser(property, user);
        if (deletedFavorite == null) {
            return;
        } else {
            property.getFavorites().remove(deletedFavorite);
            user.getFavorites().remove(deletedFavorite);
            favoriteRepository.deleteFavoriteById(deletedFavorite.getId());
        }
    }
}
