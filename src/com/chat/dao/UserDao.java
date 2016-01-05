package com.chat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.chat.model.User;

public class UserDao extends BaseDao {

    public boolean reg(User user) {
        if (this.existName(user.getName())) {
            return false;
        }
        Connection conn = getConnection();
        String sql = "insert into user values (null,?,?,?,?,?,?,'')";
        PreparedStatement ps = this.prepare(conn, sql);
        try {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getAge());
            ps.setString(4, user.getBirthday());
            ps.setInt(5, user.getSex());
            ps.setString(6, user.getImgPath());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ps);
            close(conn);
        }
    }

    public boolean existName(String name) {
        Connection conn = getConnection();
        String sql = "select * from user where name = ?";
        PreparedStatement ps = this.prepare(conn, sql);
        ResultSet rs = null;
        try {
            ps.setString(1, name);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return false;
    }

    public User login(String name, String password) {
        Connection conn = getConnection();
        String sql = "select * from user where name = ? and password = ?";
        PreparedStatement ps = this.prepare(conn, sql);
        ResultSet rs = null;
        User user = null;
        try {
            ps.setString(1, name);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                user.setSex(rs.getInt("sex"));
                user.setAge(rs.getInt("age"));
                user.setBirthday(rs.getString("birthday"));
                user.setImgPath(rs.getString("imgpath"));
                user.setSign(rs.getString("sign"));
                user.setFirends(getFriends(user));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return user;
    }

    public List<User> getFriends(User user) {
        Connection conn = getConnection();
        String sql = "select u.* from friends join user u on friends.user_id2 = u.id where friends.user_id1 = ? "
                + "union "
                + "select u.* from friends join user u on friends.user_id1 = u.id where friends.user_id2 = ?";
        PreparedStatement ps = this.prepare(conn, sql);
        ResultSet rs = null;
        List<User> list = new ArrayList<User>();
        try {
            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());
            rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setName(rs.getString("name"));
                u.setSex(rs.getInt("sex"));
                u.setAge(rs.getInt("age"));
                u.setBirthday(rs.getString("birthday"));
                u.setImgPath(rs.getString("imgpath"));
                u.setSign(rs.getString("sign"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return list;
    }

    public boolean addFriend(User user1, User user2) {
        Connection conn = getConnection();
        String sql = "insert into friends values (?,?)";
        PreparedStatement ps = this.prepare(conn, sql);
        try {
            ps.setInt(1, user1.getId());
            ps.setInt(2, user2.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ps);
            close(conn);
        }
    }

    public boolean removeFriend(User user1, User user2) {
        Connection conn = getConnection();
        String sql = "delete from friends where ( user_id1 = ? and user_id2 = ? ) or ( user_id2 = ? and user_id1 = ? )";
        PreparedStatement ps = this.prepare(conn, sql);
        try {
            ps.setInt(1, user1.getId());
            ps.setInt(2, user2.getId());
            ps.setInt(3, user1.getId());
            ps.setInt(4, user2.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ps);
            close(conn);
        }
    }

    public boolean updateUser(User user) {
        Connection conn = getConnection();
        String sql = "update user set name = ? , password = ? , sex = ? , age = ? , birthday = ? , imgPath = ? , sign = ? where id = ?";
        PreparedStatement ps = this.prepare(conn, sql);
        try {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getSex());
            ps.setInt(4, user.getAge());
            ps.setString(5, user.getBirthday());
            ps.setString(6, user.getImgPath());
            ps.setString(7, user.getSign());
            ps.setInt(8, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ps);
            close(conn);
        }
    }
}
