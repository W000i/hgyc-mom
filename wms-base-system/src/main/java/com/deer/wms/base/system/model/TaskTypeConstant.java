package com.deer.wms.base.system.model;

import com.deer.wms.base.system.model.ware.WareInfo;

public class TaskTypeConstant {
    /**
     * 任务类型
     */
    //出空箱或半箱到到操作台
    public static final int CELL_TO_OPERATOR_FLOOR = 1;
    //入空箱
    public static final int IN_NULL_BOX = 2;
    //入有货箱
    public static final int IN_AVAILABLE_BOX = 3;

    //出框到点数机
    public static final int CELL_TO_PAPER_COUNTERS = 11;
    //点数任务
    public static final int COUNT_TO_CARRIER = 12;
    //从点数机出有货框到货位
    public static final int BOX_TO_CELL_FROM_PAPER_COUNTERS = 13;
    //载具出货到出货口
    public static final int CARRIER_TO_AVG_FROM_PAPER_COUNTERS = 14;
    //从点数机出空框到货位（此时检测有货无货，无货正常上到货位，有货，框到异常处理货位）15   指定货位移动到指定货位
    public static final int CHECK_FROM_PAPER_COUNTERS = 15;

    /**
     * 盘库
     */
    //21 - 盘库任务（托盘从货位到点数机左）
    public static final int CHECK_CELL_TO_PAPER_COUNTERS_LEFT = 21;
    //22 - 盘库任务（空托盘从货位到点数机右）
    public static final int CHECK_NULL_BOX_TO_PAPER_COUNTERS_RIGHT = 22;
    //23 - 盘库任务（点数并在点完后检测左边托盘是否为空）
    public static final int CHECK_COUNT = 23;
    //24 - 盘库任务（左边托盘回空货位或者盘盈锁定货位）
    public static final int CHECK_LEFT_BOX_TO_CELL_FROM_PAPERS_COUNTERS = 24;
    //25 - 盘库任务（右边托盘从点数机到入库口）
    public static final int CHECK_RIGHT_BOX_TO_LABELER_FROM_PAPERS_COUNTERS = 25;
    //26 - 盘库任务（对右边托盘重新贴标入库）
    public static final int CHECK_TO_CELL_FROM_LABELER = 26;

    //盘点类型
    //1-手动盘点
    public static final Integer MANUAL_CHECK = 1;
    //2-自动盘点
    public static final Integer AUTO_CHECK = 2;

    //盘点状态
    //1-已下发执行中
    public static final Integer RUNNING = 1;
    //2-已完成
    public static final Integer FINISH = 2;


    /** 子库组织 */
    //311-104	基板一厂板材库(合格库)
    public static final Integer QUALIFIED = 2;
    //311-119	基板一厂板材过期库
    public static final Integer OVER_DUE = 3;
    //311-123	基板一厂板材延期库
    public static final Integer POSTPONE = 4;
    //311-124	基板一厂不合格库
    public static final Integer UNQUALIFIED = 5;
    //311-127	基板一厂责任待确认材料库
    public static final Integer SCRAP = 6;
    //311-128	基板一厂预测备料库
    public static final Integer FORECAST_PREPARATION = 7;
    //0	待检
    public static final Integer DESIRED = 1;
    //后台定时任务虚拟操作卡号
    public static final String VIRTUAL_CARD_NO = "9638520741";

    /** EBS接口调用ServiceName名称  */
    //WMS读取EBS可接收数量
    public static final String GET_LINES_ALL = "getLinesAll";
    //WMS采购接收数据写入EBS接口
    public static final String WMS_RCV_PROC = "wmsRcvProc";
    //WMS读取EBS已检验数据
    public static final String GET_TRANSACTIONS = "geTransactions";
    //WMS交货数据写入EBS
    public static final String WMS_DEV_PROC = "wmsDevProc";
    //读取EBS账户别名名称
    public static final String GET_EBS_ACCOUNT_NAME = "getEbsAccountName";
    //WMS工单发料写入EBS接口
    public static final String WMS_WIP_PROC = "wmsWipProc";
    //WMS账户别名物料发放写入EBS接口
    public static final String WMS_OTHERS_PROC = "wmsOthersProc";
    //WMS子库存转移写入EBS接口
    public static final String WMSSUBINV_TRANSPROC = "wmsSubInvProc";
    //查询账户别名
    public static final String GET_OTHERS_INTF = "getOthersIntf";
    //查询子库转移
    public static final String GET_SUBINV_INTF = "getSubinvIntf";
    //查询工单发料
    public static final String GET_WIPTRXS_INTF = "getWipTrxsIntf";
    //查询库存余量
    public static final String GET_EXISTING_STOCK = "getExistingStock";

    /** 操作类型serviceOperation */
    //查询
    public static final String QUERY = "S";
    //异步执行
    public static final String ASYNCHRONOUS_EXECUTE = "R";
    //同步执行
    public static final String SYNCHRONOUS_EXECUTE = "E";

    /** 类型ID */
    //MES工单发放
    public static final String MES_BILL_OUT = "35";
    //事务处理发放
    public static final String TRANSACTION_OUT = "31";
    //事务处理接收
    public static final String TRANSACTION_IN = "41";
    //子库转移
    public static final String SUB_INVENTORY_TRANSFER_TYPE = "2";

    /** 来源代码 */
    public static final String SOURCE = "WMS";

    /** 调用接口类型 */
    //1-采购接收
    public static final Integer RECEIVE = 1;
    //2-交货
    public static final Integer DELIVERY = 2;
    //3-工单出库
    public static final Integer OUT = 3;
    //4-账户别名事务处理
    public static final Integer ACCOUNT_ALIAS = 4;
    //5-子库转移
    public static final Integer TRANSFER = 5;

    /**  调用接口的处理结果 */
    //1-成功
    public static final Integer SUCCESS = 1;
    // 2-失败需处理
    public static final Integer FAIL_WAIT_MANAGE = 2;
    // 3-失败无需处理
    public static final Integer FAIL_NO_MANAGE = 3;
    // 4-EBS处理中
    public static final Integer MANAGING = 4;
    // 5-已处理
    public static final Integer ALREADY_MANAGE = 5;
    //6-已手动处理
    public static final Integer ALREADY_MANUAL_MANAGE = 6;

    //异常处理货位Id
    public static final Integer EXCEPTION_CELL_ID = 2187;
    public static final String EXCEPTION_MANAGE_CELL = "01013001";

    /**
     * 异常报警类型
     */
    // 1-不合格库存存储超期报警
    public static final Integer UNQUALIFIED_OVERTAKE_CAN_DELAY_DAYS = 1;
    //2-硬件报警
    public static final Integer HARDWARE_ALARM = 2;
    //3-出入库检测有无货报警
    public static final Integer DETECT_ALARM = 3;
    //呼叫AGV取载具失败
    public static final Integer CALL_AGV_ERROR = 4;
    //点数任务单箱缺少
    public static final Integer COUNT_EXILE = 5;

    //异常报警状态
    //1-未处理
    public static final Integer UNDEALT = 1;
    //2-已处理
    public static final Integer ALREADY_MANGE_ALARM = 2;



    /**
     * 领料出库类型
     */
    //工单自动出库
    public static final Integer WORKER_ORDER_OUT = 1;
    //工单手动出库
    public static final Integer WORKER_ORDER_MANUAL_OUT = 2;
    //退货出库
    public static final Integer QUIT_WAREHOUSE_OUT = 3;
    //IQC入库前库存扣减
    public static final Integer IQC_OUT = 4;
    //账户别名发放
    public static final Integer NO_WORKER_ORDER_OUT = 5;
    //非工单出库
    public static final Integer ORGANIZATION_TRANSFER = 6;
    //物料转卖
    public static final Integer ITEM_RESALE = 7;
    //账户别名接收
    public static final Integer NO_WORKER_ORDER_IN = 8;
    //退库
    public static final Integer BACK_WARE_HOUSE = 9;
    //报废出库
    public static final Integer SCRAP_OUT = 10;

    /**
     * 静态属性
     */
    //工单接口是否自动执行
    public static Integer AUTO_EXECUTE = 1;

    //仓库信息
    public static WareInfo wareInfo;
    //定时任务时间
    public static WorkerOrderIssueTime workerOrderIssueTime;
    //库存组织Id
    public static Integer organizationId = 308;

    /**
     * 报警指定账户
     */
    public static final Integer ALARM_ASSIGN_ACCOUNT = 12;

    public static int GET_EMPTY_BOX_STATE = 1;
    public static int call_agv_state = 1;
}
