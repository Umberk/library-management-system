package com.cya.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.cya.dao.BookMapper;
import com.cya.entity.Book;
//import com.cya.repos.BookRepository;
import com.cya.util.JdbcUtil;
import com.cya.util.vo.BookOut;
import com.cya.util.vo.PageOut;
import com.cya.util.consts.ConvertUtil;
import com.cya.util.ro.PageIn;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class BookService {

//    @Autowired
//    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;


    /**
     * 添加用户
     * @param book 图书
     * @return 返回添加的图书
     */
//    public Book addBook(Book book) {
//        return bookRepository.saveAndFlush(book);
//    }
    public Book addBook(Book book) {
        // JDBC连接参数
        try (Connection connection=JdbcUtil.getConnection()){
            // SQL插入语句
            String insertSql = "INSERT INTO book (isbn, name, author, pages, translate, publish, price, size, type, publish_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            ) {
                // 设置插入语句中的参数值
                preparedStatement.setString(1, book.getIsbn());
                preparedStatement.setString(2, book.getName());
                preparedStatement.setString(3, book.getAuthor());
                preparedStatement.setInt(4, book.getPages());
                preparedStatement.setString(5, book.getTranslate());
                preparedStatement.setString(6, book.getPublish());
                preparedStatement.setDouble(7, book.getPrice());
                preparedStatement.setInt(8, book.getSize());
                preparedStatement.setString(9, book.getType());
                preparedStatement.setDate(10, new java.sql.Date(book.getPublishTime().getTime()));


                // 执行插入操作
                int rowsInserted = preparedStatement.executeUpdate();

                // 输出插入的行数
                System.out.println("Rows inserted: " + rowsInserted);

                // 如果需要获取插入后的自增主键值，可以使用下面的方式
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // 获取自增主键值
                        int generatedId = generatedKeys.getInt(1);
                        book.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return book;
    }

    /**
     * 编辑用户
     * @param book 图书对象
     * @return true or false
     */
    public boolean updateBook(Book book) {
        return bookMapper.updateBook(BeanUtil.beanToMap(book))>0;
    }

    /**
     * 图书详情
     * @param id 主键
     * @return 图书详情
     */
//    public BookOut findBookById(Integer id) {
//        Optional<Book> optional = bookRepository.findById(id);
//        if (optional.isPresent()) {
//            Book book = optional.get();
//            BookOut out = new BookOut();
//            BeanUtil.copyProperties(book,out);
//            out.setPublishTime(DateUtil.format(book.getPublishTime(),"yyyy-MM-dd"));
//            return out;
//        }
//        return null;
//    }
    public BookOut findBookById(Integer id) {
        // JDBC连接参数
        try (
                Connection connection = JdbcUtil.getConnection();
        ) {
            // SQL查询语句
            String selectSql = "SELECT * FROM book WHERE id = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ) {
                // 设置查询语句中的参数值
                preparedStatement.setInt(1, id);

                // 执行查询操作
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // 映射结果到 BookOut 对象
                        BookOut out = new BookOut();
                        out.setId(resultSet.getInt("id"));
                        out.setIsbn(resultSet.getString("isbn"));
                        out.setName(resultSet.getString("name"));
                        out.setAuthor(resultSet.getString("author"));
                        out.setPages(resultSet.getInt("pages"));
                        out.setTranslate(resultSet.getString("translate"));
                        out.setPublish(resultSet.getString("publish"));
                        out.setPrice(resultSet.getDouble("price"));
                        out.setSize(resultSet.getInt("size"));
                        out.setType(resultSet.getString("type"));

                        // 格式化日期
                        String publishTime = DateUtil.format(resultSet.getDate("publish_time"), "yyyy-MM-dd");
                        out.setPublishTime(publishTime);

                        return out;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    //    public Book findBook(Integer id) {
//        Optional<Book> optional = bookRepository.findById(id);
//        if (optional.isPresent()) {
//            return optional.get();
//        }
//        return null;
//    }
    public Book findBook(Integer id) {
        // JDBC连接参数
        try (
                Connection connection = JdbcUtil.getConnection();
        ) {
            // SQL查询语句
            String selectSql = "SELECT * FROM book WHERE id = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ) {
                // 设置查询语句中的参数值
                preparedStatement.setInt(1, id);

                // 执行查询操作
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // 映射结果到 Book 对象
                        Book book = new Book();
                        book.setId(resultSet.getInt("id"));
                        book.setIsbn(resultSet.getString("isbn"));
                        book.setName(resultSet.getString("name"));
                        book.setAuthor(resultSet.getString("author"));
                        book.setPages(resultSet.getInt("pages"));
                        book.setTranslate(resultSet.getString("translate"));
                        book.setPublish(resultSet.getString("publish"));
                        book.setPrice(resultSet.getDouble("price"));
                        book.setSize(resultSet.getInt("size"));
                        book.setType(resultSet.getString("type"));
                        book.setPublishTime(resultSet.getDate("publish_time"));

                        return book;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ISBN查询
     * @param isbn
     * @return
     */
//    public BookOut findBookByIsbn(String isbn) {
//        Book book = bookRepository.findByIsbn(isbn);
//        BookOut out = new BookOut();
//        if (book == null) {
//            return out;
//        }
//        BeanUtil.copyProperties(book,out);
//        out.setPublishTime(DateUtil.format(book.getPublishTime(),"yyyy-MM-dd"));
//        return out;
//    }
    public BookOut findBookByIsbn(String isbn) {
        // JDBC连接参数
        try (
                Connection connection = JdbcUtil.getConnection();
        ) {
            // SQL查询语句
            String selectSql = "SELECT * FROM book WHERE isbn = ?";

            try (
                    // 创建 PreparedStatement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
            ) {
                // 设置查询语句中的参数值
                preparedStatement.setString(1, isbn);

                // 执行查询操作
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // 映射结果到 BookOut 对象
                        BookOut out = new BookOut();
                        out.setId(resultSet.getInt("id"));
                        out.setIsbn(resultSet.getString("isbn"));
                        out.setName(resultSet.getString("name"));
                        out.setAuthor(resultSet.getString("author"));
                        out.setPages(resultSet.getInt("pages"));
                        out.setTranslate(resultSet.getString("translate"));
                        out.setPublish(resultSet.getString("publish"));
                        out.setPrice(resultSet.getDouble("price"));
                        out.setSize(resultSet.getInt("size"));
                        out.setType(resultSet.getString("type"));

                        // 格式化日期
                        String publishTime = DateUtil.format(resultSet.getDate("publish_time"), "yyyy-MM-dd");
                        out.setPublishTime(publishTime);

                        return out;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new BookOut();
    }


    /**
     * 删除图书
     * @param id 主键
     * @return true or false
     */
//    public void deleteBook(Integer id) {
//        bookRepository.deleteById(id);
//    }
    public void deleteBook(Integer id) {
        // JDBC连接参数
        try (
                Connection connection = JdbcUtil.getConnection();
        ) {
            // SQL删除语句
            String deleteSql = "DELETE FROM book WHERE id = ?";

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
     * 图书搜索查询(mybatis 分页)
     * @param pageIn
     * @return
     */
    public PageOut getBookList(PageIn pageIn) {

        PageHelper.startPage(pageIn.getCurrPage(),pageIn.getPageSize());
        List<Book> list = bookMapper.findBookListByLike(pageIn.getKeyword());
        PageInfo<Book> pageInfo = new PageInfo<>(list);

        List<BookOut> bookOuts = new ArrayList<>();
        for (Book book : pageInfo.getList()) {
            BookOut out = new BookOut();
            BeanUtil.copyProperties(book,out);
            out.setPublishTime(DateUtil.format(book.getPublishTime(),"yyyy-MM-dd"));
            out.setType(ConvertUtil.typeStr(book.getType()));
            bookOuts.add(out);
        }

        // 自定义分页返回对象
        PageOut pageOut = new PageOut();
        pageOut.setList(bookOuts);
        pageOut.setTotal((int)pageInfo.getTotal());
        pageOut.setCurrPage(pageInfo.getPageNum());
        pageOut.setPageSize(pageInfo.getPageSize());
        return pageOut;
    }


}
