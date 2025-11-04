package com.cya.service;

import com.cya.util.JdbcUtil;
import cn.hutool.core.bean.BeanUtil;
import com.cya.dao.UsersMapper;
import com.cya.entity.Users;
//import com.cya.repos.UsersRepository;
import com.cya.util.ro.PageIn;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;
/**
 * @Description 用户业务类
 * @Date 2022/9/4 16:35
 * @Author by 公众号【IT学长】
 */
@Service
public class UserService implements UserDetailsService{

//    @Autowired
//    private UsersRepository usersRepository;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取所有用户, 分页(鉴于jpa分页过于繁琐, 已使用mybatis, page helper分页, 此方法弃用)
     * @param pageable 分页对象
     */
//    public Page<Users> getUsers(String keyword,Pageable pageable) {
//        return usersRepository.findByUsernameLike(keyword,pageable);
//    }

    public Page<Users> getUsers(String keyword, Pageable pageable) {
        List<Users> usersList = new ArrayList<>();
        int total = 0;

        try (Connection connection = JdbcUtil.getConnection()) {
            String countSql = "SELECT COUNT(*) FROM users WHERE username LIKE ?";
            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                countStatement.setString(1, "%" + keyword + "%");

                try (ResultSet countResultSet = countStatement.executeQuery()) {
                    if (countResultSet.next()) {
                        total = countResultSet.getInt(1);
                    }
                }
            }

            String selectSql = "SELECT * FROM users WHERE username LIKE ? LIMIT ? OFFSET ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
                preparedStatement.setString(1, "%" + keyword + "%");
                preparedStatement.setInt(2, pageable.getPageSize());
                preparedStatement.setLong(3, pageable.getOffset());/////////////////////Long? Int?

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Users user = new Users();
                        user.setId(resultSet.getInt("id"));
                        user.setAvatar(resultSet.getString("avatar"));
                        user.setNickname(resultSet.getString("nickname"));
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setBirthday(resultSet.getDate("birthday"));
                        user.setIsAdmin(resultSet.getInt("is_admin"));
                        user.setTel(resultSet.getString("tel"));
                        user.setEmail(resultSet.getString("email"));
                        user.setAddress(resultSet.getString("address"));
                        user.setSize(resultSet.getInt("size"));

                        usersList.add(user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常...
        }

        return new PageImpl<>(usersList, pageable, total);
    }

    /**
     * 登录 (使用SpringSecurity 此方法弃用)
     * @param username 用户名
     * @param password 密码
     */
//    public Users login(String username,String password) {
//       return usersRepository.findByUsernameAndPassword(username,password);
//    }

    public Users login(String username, String password) {
        try (Connection connection = JdbcUtil.getConnection()) {
            String selectSql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Users user = new Users();
                        user.setId(resultSet.getInt("id"));
                        user.setAvatar(resultSet.getString("avatar"));
                        user.setNickname(resultSet.getString("nickname"));
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setBirthday(resultSet.getDate("birthday"));
                        user.setIsAdmin(resultSet.getInt("is_admin"));
                        user.setTel(resultSet.getString("tel"));
                        user.setEmail(resultSet.getString("email"));
                        user.setAddress(resultSet.getString("address"));
                        user.setSize(resultSet.getInt("size"));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常...
        }

        return null;
    }

    /**
     * 添加用户
     * @param users 用户
     * @return 返回添加的用户
     */
//    public Users addUser(Users users) {
//        return usersRepository.saveAndFlush(users);
//    }

    public Users addUser(Users users) {
        try (Connection connection = JdbcUtil.getConnection()) {
            String insertSql = "INSERT INTO users (avatar, nickname, username, password, birthday, is_admin, tel, email, address, size) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, users.getAvatar());
                preparedStatement.setString(2, users.getNickname());
                preparedStatement.setString(3, users.getUsername());
                preparedStatement.setString(4, users.getPassword());
                preparedStatement.setDate(5, users.getBirthday() != null ? new java.sql.Date(users.getBirthday().getTime()) : null);
                preparedStatement.setInt(6, users.getIsAdmin());
                preparedStatement.setString(7, users.getTel());
                preparedStatement.setString(8, users.getEmail());
                preparedStatement.setString(9, users.getAddress());
                preparedStatement.setInt(10, users.getSize());

                // 执行插入操作
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // 获取生成的主键
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            users.setId(generatedId);
                            return users;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常...
        }

        return null;
    }

    /**
     * 编辑用户
     * @param users 用户对象
     * @return true or false
     */
//    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(Users users) {
        return usersMapper.updateUsers(BeanUtil.beanToMap(users))>0;
    }

    /**
     * 用户详情
     * @param id 主键
     * @return 用户详情
     */
//    public Users findUserById(Integer id) {
//        Optional<Users> optional = usersRepository.findById(id);
//        if (optional.isPresent()) {
//            return optional.get();
//        }
//        return null;
//    }

    public Users findUserById(Integer id) {
        try (Connection connection = JdbcUtil.getConnection()) {
            String selectSql = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Users user = new Users();
                        user.setId(resultSet.getInt("id"));
                        user.setAvatar(resultSet.getString("avatar"));
                        user.setNickname(resultSet.getString("nickname"));
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setBirthday(resultSet.getDate("birthday"));
                        user.setIsAdmin(resultSet.getInt("is_admin"));
                        user.setTel(resultSet.getString("tel"));
                        user.setEmail(resultSet.getString("email"));
                        user.setAddress(resultSet.getString("address"));
                        user.setSize(resultSet.getInt("size"));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常...
        }

        return null;
    }

    /**
     * 删除用户
     * @param id 主键
     * @return true or false
     */
//    public void deleteUser(Integer id) {
//        usersRepository.deleteById(id);
//    }

    public void deleteUser(Integer id) {
        try (Connection connection = JdbcUtil.getConnection()) {
            String deleteSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
                preparedStatement.setInt(1, id);

                // 执行删除操作
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常...
        }
    }

    /**
     * 用户搜索查询(mybatis 分页)
     * @param pageIn
     * @return
     */
    public PageInfo<Users> getUserList(PageIn pageIn) {

        PageHelper.startPage(pageIn.getCurrPage(),pageIn.getPageSize());
        List<Users> listByLike = usersMapper.findListByLike(pageIn.getKeyword());
        return new PageInfo<>(listByLike);
    }

    /**
     * 用户鉴权
     * @param username 用户名
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查找用户

        Users user = findByUsername(username);
        // 获得角色
        String role = String.valueOf(user.getIsAdmin());
        // 角色集合
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 角色必须以`ROLE_`开头，数据库中没有，则在这里加
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        // 数据库密码是明文, 需要加密进行比对
        return new User(user.getUsername(), passwordEncoder.encode(user.getPassword()), authorities);
    }

    /**
     * 用户名查询用户信息
     * @param username 用户名
     */
//    public Users findByUsername(String username) {
//        return usersRepository.findByUsername(username);
//    }

    public  Users findByUsername(String username) {
        try (Connection connection = JdbcUtil.getConnection()) {
            String selectSql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectSql)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Users user = new Users();
                        user.setId(resultSet.getInt("id"));
                        user.setAvatar(resultSet.getString("avatar"));
                        user.setNickname(resultSet.getString("nickname"));
                        user.setUsername(resultSet.getString("username"));
                        user.setPassword(resultSet.getString("password"));
                        user.setBirthday(resultSet.getDate("birthday"));
                        user.setIsAdmin(resultSet.getInt("is_admin"));
                        user.setTel(resultSet.getString("tel"));
                        user.setEmail(resultSet.getString("email"));
                        user.setAddress(resultSet.getString("address"));
                        user.setSize(resultSet.getInt("size"));

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // 处理异常...
        }

        return null;
    }

}
