package com.github.ljl.framework.winter.jdbc.transaction;

import com.github.ljl.framework.winter.jdbc.transaction.bean.DataSourceTransactionManager;

import java.sql.Connection;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 16:12
 **/

public class TransactionalUtils {
    public static Connection getCurrentConnection() {
        TransactionStatus ts = DataSourceTransactionManager.transactionStatus.get();
        return ts == null ? null : ts.connection;
    }
}
