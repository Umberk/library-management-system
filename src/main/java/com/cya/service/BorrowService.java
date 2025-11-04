package com.cya.service;

import com.cya.dao.BorrowMapper;
import com.cya.entity.Book;
import com.cya.entity.Borrow;
import com.cya.entity.Users;
//import com.cya.repos.BorrowRepository;
import com.cya.util.JdbcUtil;
import com.cya.util.consts.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @Description 借阅管理
 */
@Service
public class BorrowService {

//    @Autowired
//    private BorrowRepository borrowRepository;

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    /**
         * 添加
     */
    @Transactional
    public Integer addBorrow(Borrow borrow) {
        Book book = bookService.findBook(borrow.getBookId());
        Users users = userService.findUserById(borrow.getUserId());

        // 查询是否已经借阅过该图书
        Borrow bor = findBorrowByUserIdAndBookId(users.getId(),book.getId());
        if (bor!=null) {
            Integer ret = bor.getRet();
            if (ret!=null) {
                // 已借阅, 未归还 不可再借
                if (ret == Constants.NO) {
                    return Constants.BOOK_BORROWED;
                }
            }
        }

        // 库存数量减一
        int size = book.getSize();
        if (size>0) {
            size--;
            book.setSize(size);
            bookService.updateBook(book);
        }else {
            return Constants.BOOK_SIZE_NOT_ENOUGH;
        }

        // 用户可借数量减一
        int userSize = users.getSize();
        if (userSize>0) {
            userSize --;
            users.setSize(userSize);
            userService.updateUser(users);
        }else {
            return Constants.USER_SIZE_NOT_ENOUGH;
        }


//        // 添加借阅信息, 借阅默认为未归还状态
//        borrow.setRet(Constants.NO);
//        borrowRepository.saveAndFlush(borrow);
        // 添加借阅信息，借阅默认为未归还状态
        borrow.setRet(Constants.NO);

// JDBC连接参数
        try (Connection connection = JdbcUtil.getConnection()) {
            // SQL插入语句
            String insertSql = "INSERT INTO borrow (user_id, book_id, create_time, end_time, update_time, ret) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ) {
                // 设置插入语句中的参数值
                preparedStatement.setInt(1, borrow.getUserId());
                preparedStatement.setInt(2, borrow.getBookId());
                preparedStatement.setTimestamp(3, new java.sql.Timestamp(borrow.getCreateTime().getTime()));
                preparedStatement.setTimestamp(4, new java.sql.Timestamp(borrow.getEndTime().getTime()));
                preparedStatement.setTimestamp(5, new java.sql.Timestamp(borrow.getUpdateTime().getTime()));
                preparedStatement.setInt(6, borrow.getRet());

                // 执行插入操作
                int rowsInserted = preparedStatement.executeUpdate();

                // 输出插入的行数
                System.out.println("Rows inserted: " + rowsInserted);

                // 如果需要获取插入后的自增主键值，可以使用下面的方式
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // 获取自增主键值
                        int generatedId = generatedKeys.getInt(1);
                        borrow.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // 一切正常
        return Constants.OK;
    }

    /**
     * user id查询所有借阅信息
     */
//    public List<Borrow> findAllBorrowByUserId(Integer userId) {
//        return borrowRepository.findBorrowByUserId(userId);
//    }
    public List<Borrow> findAllBorrowByUserId(Integer userId) {
        // JDBC连接参数
        List<Borrow> borrowList = new ArrayList<>();

        try (Connection connection = JdbcUtil.getConnection()) {
            // SQL查询语句
            String selectSql = "SELECT * FROM borrow WHERE user_id = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ) {
                // 设置查询语句中的参数值
                preparedStatement.setInt(1, userId);

                // 执行查询操作
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // 映射结果到 Borrow 对象
                        Borrow borrow = new Borrow();
                        borrow.setId(resultSet.getInt("id"));
                        borrow.setUserId(resultSet.getInt("user_id"));
                        borrow.setBookId(resultSet.getInt("book_id"));
                        borrow.setCreateTime(resultSet.getTimestamp("create_time"));
                        borrow.setEndTime(resultSet.getTimestamp("end_time"));
                        borrow.setUpdateTime(resultSet.getTimestamp("update_time"));
                        borrow.setRet(resultSet.getInt("ret"));

                        borrowList.add(borrow);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrowList;
    }

    /**
     * user id查询所有 已借阅信息
     */
//    public List<Borrow> findBorrowsByUserIdAndRet(Integer userId, Integer ret) {
//        return borrowRepository.findBorrowsByUserIdAndRet(userId,ret);
//    }
    public List<Borrow> findBorrowsByUserIdAndRet(Integer userId, Integer ret) {
        List<Borrow> borrows = new ArrayList<>();

        // JDBC连接参数
        try (Connection connection = JdbcUtil.getConnection()) {
            // SQL查询语句
            String selectSql = "SELECT * FROM borrow WHERE user_id = ? AND ret = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ) {
                // 设置查询语句中的参数值
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, ret);

                // 执行查询操作
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // 映射结果到 Borrow 对象
                        Borrow borrow = new Borrow();
                        borrow.setId(resultSet.getInt("id"));
                        borrow.setUserId(resultSet.getInt("user_id"));
                        borrow.setBookId(resultSet.getInt("book_id"));
                        borrow.setCreateTime(resultSet.getTimestamp("create_time"));
                        borrow.setEndTime(resultSet.getTimestamp("end_time"));
                        borrow.setUpdateTime(resultSet.getTimestamp("update_time"));
                        borrow.setRet(resultSet.getInt("ret"));

                        borrows.add(borrow);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrows;
    }


    /**
         * 详情
     */
//    public Borrow findById(Integer id) {
//        Optional<Borrow> optional = borrowRepository.findById(id);
//        if (optional.isPresent()) {
//            return optional.get();
//        }
//        return null;
//    }
    public Borrow findById(Integer id) {
        Borrow borrow = null;

        // JDBC连接参数
        try (Connection connection = JdbcUtil.getConnection()) {
            // SQL查询语句
            String selectSql = "SELECT * FROM borrow WHERE id = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ) {
                // 设置查询语句中的参数值
                preparedStatement.setInt(1, id);

                // 执行查询操作
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // 映射结果到 Borrow 对象
                        borrow = new Borrow();
                        borrow.setId(resultSet.getInt("id"));
                        borrow.setUserId(resultSet.getInt("user_id"));
                        borrow.setBookId(resultSet.getInt("book_id"));
                        borrow.setCreateTime(resultSet.getTimestamp("create_time"));
                        borrow.setEndTime(resultSet.getTimestamp("end_time"));
                        borrow.setUpdateTime(resultSet.getTimestamp("update_time"));
                        borrow.setRet(resultSet.getInt("ret"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrow;
    }

    /**
         * 编辑
     */
    public boolean updateBorrow(Borrow borrow) {
        return borrowMapper.updateBorrow(borrow)>0;
    }


    /**
         * 编辑
     */
//    public Borrow updateBorrowByRepo(Borrow borrow) {
//        return borrowRepository.saveAndFlush(borrow);
//    }
    public Borrow updateBorrowByJdbc(Borrow borrow) {
        try (Connection connection=JdbcUtil.getConnection()) {
            // SQL更新语句
            String updateSql = "UPDATE borrow SET user_id=?, book_id=?, create_time=?, end_time=?, update_time=?, ret=? WHERE id=?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                // 设置更新语句中的参数值
                preparedStatement.setInt(1, borrow.getUserId());
                preparedStatement.setInt(2, borrow.getBookId());

                // 假设 Borrow 类中的 createTime、endTime 和 updateTime 是 Date 对象
                preparedStatement.setTimestamp(3, new java.sql.Timestamp(borrow.getCreateTime().getTime()));
                preparedStatement.setTimestamp(4, new java.sql.Timestamp(borrow.getEndTime().getTime()));
                preparedStatement.setTimestamp(5, new java.sql.Timestamp(borrow.getUpdateTime().getTime()));

                preparedStatement.setInt(6, borrow.getRet());
                preparedStatement.setInt(7, borrow.getId());

                // 执行更新操作
                int rowsUpdated = preparedStatement.executeUpdate();

                // 输出更新的行数
                System.out.println("Rows updated: " + rowsUpdated);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return borrow;
    }

    /**
         * 根据ID删除
     */
//    public void deleteBorrow(Integer id) {
//        borrowRepository.deleteById(id);
//    }
    public void deleteBorrow(Integer id) {
        // JDBC连接参数
        try (Connection connection = JdbcUtil.getConnection()) {
            // SQL删除语句
            String deleteSql = "DELETE FROM borrow WHERE id = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(deleteSql);
            ) {
                // 设置删除语句中的参数值
                preparedStatement.setInt(1, id);

                // 执行删除操作
                int rowsDeleted = preparedStatement.executeUpdate();

                // 输出删除的行数
                System.out.println("Rows deleted: " + rowsDeleted);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
         * 根据book_id删除
     */
    public void deleteBorrowByBookId(Integer bookId) {
    	borrowMapper.deleteBorrowByBookId(bookId);
    }
    

    /**
         * 查询用户某一条借阅信息
     * @param userId 用户id
     * @param bookId 图书id
     */
    public Borrow findBorrowByUserIdAndBookId(int userId,int bookId) {
        return borrowMapper.findBorrowByUserIdAndBookId(userId,bookId);
    }

    /**
         * 归还书籍,
     * @param userId 用户Id
     * @param bookId 书籍id
     */
    @Transactional(rollbackFor = Exception.class)
    public void retBook(int userId,int bookId) {
        // 用户可借数量加1
        Users user = userService.findUserById(userId);
        Integer size = user.getSize();
        size++;
        user.setSize(size);
        userService.updateUser(user);


        // 书籍库存加1
        Book book = bookService.findBook(bookId);
        Integer bookSize = book.getSize();
        bookSize++;
        book.setSize(bookSize);
        bookService.updateBook(book);
        // 借阅记录改为已归还,删除记录
        Borrow borrow = this.findBorrowByUserIdAndBookId(userId, bookId);
        this.deleteBorrow(borrow.getId());
    }
}
