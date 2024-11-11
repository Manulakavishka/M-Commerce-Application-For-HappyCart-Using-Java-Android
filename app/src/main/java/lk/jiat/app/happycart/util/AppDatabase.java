package lk.jiat.app.happycart.util;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import lk.jiat.app.happycart.dao.UserDao;
import lk.jiat.app.happycart.entity.User;

@Database(entities = {User.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
