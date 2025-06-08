package com.hotproperties.hotproperties.util;

import com.hotproperties.hotproperties.entity.User;
import org.springframework.security.core.Authentication;

public record CurrentUserContext(User user, Authentication auth) {}
