package lk.jiat.app.happycart.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import lk.jiat.app.happycart.entity.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Insert
    long[] insertAll(User... user);

    @Update
    int update(User user);

    @Delete
    int delete (User user);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE id=:id")
    User getById(int id);

    @Query("SELECT * FROM User WHERE email=:email")
    User findByEmail(String email);
}
