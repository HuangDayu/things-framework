package cn.huangdayu.things.common.constants;

/**
 * @author huangdayu
 */
public final class ThingsConstants {

    /**
     * 物模型引擎通配符
     */
    public static final String THINGS_WILDCARD = "_THINGS_WILDCARD_";

    /**
     * 物模型分隔符
     */
    public static final String THINGS_SEPARATOR = "#";
    
    public static final String THINGS_DOT = ".";

    /**
     * 物联网设备事件标识
     *
     * @author huangdayu
     */
    public static final class Events {

        /**
         * 普通事件：设备上线
         */
        public static final String DEVICE_ONLINE = "deviceOnline";

        /**
         * 普通事件：设备下线
         */
        public static final String DEVICE_OFFLINE = "deviceOffline";

        /**
         * 普通事件：设备心跳
         */
        public static final String DEVICE_HEARTBEAT = "deviceHeartbeat";

        /**
         * 普通事件：设备状态报告
         */
        public static final String DEVICE_STATUS_REPORT = "deviceStatusReport";

        /**
         * 普通事件：设备配置更新
         */
        public static final String DEVICE_CONFIGURATION_UPDATE = "deviceConfigurationUpdate";

        /**
         * 普通事件：设备固件更新
         */
        public static final String DEVICE_FIRMWARE_UPDATE = "deviceFirmwareUpdate";

        /**
         * 普通事件：设备时间同步
         */
        public static final String DEVICE_TIME_SYNC = "deviceTimeSync";

        /**
         * 普通事件：设备接收到命令
         */
        public static final String DEVICE_COMMAND_RECEIVED = "deviceCommandReceived";

        /**
         * 普通事件：设备执行命令完成
         */
        public static final String DEVICE_COMMAND_EXECUTED = "deviceCommandExecuted";

        /**
         * 普通事件：设备数据上传
         */
        public static final String DEVICE_DATA_UPLOAD = "deviceDataUpload";

        /**
         * 普通事件：设备数据下载
         */
        public static final String DEVICE_DATA_DOWNLOAD = "deviceDataDownload";

        /**
         * 普通事件：设备维护
         */
        public static final String DEVICE_MAINTENANCE = "deviceMaintenance";

        /**
         * 普通事件：设备重置
         */
        public static final String DEVICE_RESET = "deviceReset";

        /**
         * 普通事件：设备位置更新
         */
        public static final String DEVICE_LOCATION_UPDATE = "deviceLocationUpdate";

        /**
         * 普通事件：设备健康检查
         */
        public static final String DEVICE_HEALTH_CHECK = "deviceHealthCheck";

        /**
         * 普通事件：场景激活
         */
        public static final String SCENE_ACTIVATED = "sceneActivated";

        /**
         * 普通事件：场景取消激活
         */
        public static final String SCENE_DEACTIVATED = "sceneDeactivated";

        /**
         * 普通事件：定时任务设定
         */
        public static final String SCHEDULE_SET = "scheduleSet";

        /**
         * 普通事件：定时任务取消
         */
        public static final String SCHEDULE_CANCELLED = "scheduleCancelled";

        /**
         * 普通事件：有人回家
         */
        public static final String PERSON_ARRIVED_HOME = "personArrivedHome";

        /**
         * 普通事件：有人离开家
         */
        public static final String PERSON_LEFT_HOME = "personLeftHome";

        /**
         * 告警事件：设备故障
         */
        public static final String DEVICE_MALFUNCTION = "deviceMalfunction";

        /**
         * 告警事件：设备被篡改
         */
        public static final String DEVICE_TAMPERING = "deviceTampering";

        /**
         * 告警事件：设备过载
         */
        public static final String DEVICE_OVERLOAD = "deviceOverload";

        /**
         * 告警事件：设备电池电量低
         */
        public static final String DEVICE_BATTERY_LOW = "deviceBatteryLow";

        /**
         * 告警事件：设备通信错误
         */
        public static final String DEVICE_COMMUNICATION_ERROR = "deviceCommunicationError";

        /**
         * 告警事件：设备安全漏洞
         */
        public static final String DEVICE_SECURITY_BREACH = "deviceSecurityBreach";

        /**
         * 告警事件：设备温度超出范围
         */
        public static final String DEVICE_TEMPERATURE_OUT_OF_RANGE = "deviceTemperatureOutOfRange";

        /**
         * 告警事件：设备湿度超出范围
         */
        public static final String DEVICE_HUMIDITY_OUT_OF_RANGE = "deviceHumidityOutOfRange";

        /**
         * 告警事件：设备检测到运动
         */
        public static final String DEVICE_MOTION_DETECTED = "deviceMotionDetected";

        /**
         * 告警事件：设备围栏违规
         */
        public static final String DEVICE_PERIMETER_VIOLATION = "devicePerimeterViolation";

        /**
         * 告警事件：设备异常活动
         */
        public static final String DEVICE_UNUSUAL_ACTIVITY = "deviceUnusualActivity";

        /**
         * 告警事件：设备软件错误
         */
        public static final String DEVICE_SOFTWARE_ERROR = "deviceSoftwareError";

        /**
         * 告警事件：设备硬件故障
         */
        public static final String DEVICE_HARDWARE_FAILURE = "deviceHardwareFailure";

        /**
         * 告警事件：设备网络中断
         */
        public static final String DEVICE_NETWORK_DISRUPTION = "deviceNetworkDisruption";

        /**
         * 告警事件：水位过高
         */
        public static final String WATER_LEVEL_HIGH = "waterLevelHigh";

        /**
         * 告警事件：水位过低
         */
        public static final String WATER_LEVEL_LOW = "waterLevelLow";

        /**
         * 告警事件：水滴检测
         */
        public static final String WATER_DRIP_DETECTED = "waterDripDetected";

        /**
         * 告警事件：土壤酸碱度超出范围
         */
        public static final String SOIL_PH_OUT_OF_RANGE = "soilpHOutOfRange";

        /**
         * 告警事件：土壤湿度超出范围
         */
        public static final String SOIL_MOISTURE_OUT_OF_RANGE = "soilMoistureOutOfRange";

        /**
         * 告警事件：空气质量差
         */
        public static final String AIR_QUALITY_POOR = "airQualityPoor";

        /**
         * 告警事件：光照强度超出范围
         */
        public static final String LIGHT_INTENSITY_OUT_OF_RANGE = "lightIntensityOutOfRange";

        /**
         * 告警事件：传感器故障
         */
        public static final String SENSOR_FAILURE = "sensorFailure";

        /**
         * 告警事件：断电
         */
        public static final String POWER_OUTAGE = "powerOutage";

        /**
         * 告警事件：电力恢复
         */
        public static final String POWER_RESTORED = "powerRestored";

        /**
         * 告警事件：有人摔倒
         */
        public static final String PERSON_FALL_DETECTED = "personFallDetected";

        /**
         * 告警事件：婴儿啼哭
         */
        public static final String BABY_CRYING = "babyCrying";

        /**
         * 安全事件：火警
         */
        public static final String FIRE_ALARM = "fireAlarm";

        /**
         * 安全事件：匪警
         */
        public static final String BURGLARY_ALARM = "burglaryAlarm";

        /**
         * 安全事件：燃气泄漏报警
         */
        public static final String GAS_LEAK_ALARM = "gasLeakAlarm";

        /**
         * 安全事件：烟雾报警
         */
        public static final String SMOKE_DETECTED = "smokeDetected";

        /**
         * 安全事件：入侵报警
         */
        public static final String INTRUSION_DETECTED = "intrusionDetected";

        /**
         * 安全事件：紧急呼叫
         */
        public static final String EMERGENCY_CALL = "emergencyCall";

        /**
         * 安全事件：安防系统启动
         */
        public static final String SECURITY_SYSTEM_ARMED = "securitySystemArmed";

        /**
         * 安全事件：安防系统解除
         */
        public static final String SECURITY_SYSTEM_DISARMED = "securitySystemDisarmed";

        /**
         * 安全事件：门打开
         */
        public static final String DOOR_OPEN = "doorOpen";

        /**
         * 安全事件：窗户打开
         */
        public static final String WINDOW_OPEN = "windowOpen";

        /**
         * 安全事件：运动检测
         */
        public static final String MOTION_DETECTED = "motionDetected";

        /**
         * 安全事件：防篡改警报
         */
        public static final String TAMPER_ALERT = "tamperAlert";

        /**
         * 安全事件：未经授权访问
         */
        public static final String UNAUTHORIZED_ACCESS = "unauthorizedAccess";

        /**
         * 自动化事件：自动化触发
         */
        public static final String AUTOMATION_TRIGGERED = "automationTriggered";

        /**
         * 自动化事件：自动化完成
         */
        public static final String AUTOMATION_COMPLETED = "automationCompleted";

        /**
         * 自动化事件：自动化失败
         */
        public static final String AUTOMATION_FAILED = "automationFailed";

        /**
         * 自动化事件：自动化禁用
         */
        public static final String AUTOMATION_DISABLED = "automationDisabled";

        /**
         * 自动化事件：自动化启用
         */
        public static final String AUTOMATION_ENABLED = "automationEnabled";


    }

    /**
     * 物联网设备属性标识
     *
     * @author huangdayu
     */
    public static final class Properties {

        /**
         * 名称
         */
        public static final String NAME = "name";

        /**
         * 连接状态
         */
        public static final String CONNECTIVITY = "connectivity";

        /**
         * 亮度
         */
        public static final String BRIGHTNESS = "brightness";

        /**
         * 电源状态
         */
        public static final String POWER_STATE = "powerState";

        /**
         * 电量水平
         */
        public static final String POWER_LEVEL = "powerLevel";

        /**
         * 温度
         */
        public static final String TEMPERATURE = "temperature";

        /**
         * 模式
         */
        public static final String MODE = "mode";

        /**
         * 湿度
         */
        public static final String HUMIDITY = "humidity";

        /**
         * 空气质量
         */
        public static final String AIR_QUALITY = "airQuality";

        /**
         * PM2.5
         */
        public static final String PM2D5 = "pm2d5";

        /**
         * 二氧化碳
         */
        public static final String CO2 = "co2";

        /**
         * 总挥发性有机化合物
         */
        public static final String TOVC = "tovic";

        /**
         * 甲醛
         */
        public static final String FORMALDEHYDE = "formaldehyde";

        /**
         * 百分比
         */
        public static final String PERCENTAGE = "percentage";

        /**
         * 颜色
         */
        public static final String COLOR = "color";

        /**
         * 色温(开尔文)
         */
        public static final String COLOR_TEMPERATURE_IN_KELVIN = "colorTemperatureInKelvin";

        /**
         * 日期时间
         */
        public static final String DATE_TIME = "dateTime";

        /**
         * 开机状态
         */
        public static final String TURN_ON_STATE = "turnOnState";

        /**
         * 暂停状态
         */
        public static final String PAUSE_STATE = "pauseState";

        /**
         * 锁定状态
         */
        public static final String LOCK_STATE = "lockState";

        /**
         * 电量容量
         */
        public static final String ELECTRICITY_CAPACITY = "electricityCapacity";

        /**
         * 油量容量
         */
        public static final String OIL_CAPACITY = "oilCapacity";

        /**
         * 行驶距离
         */
        public static final String DRIVING_DISTANCE = "drivingDistance";

        /**
         * 风扇速度
         */
        public static final String FAN_SPEED = "fanSpeed";

        /**
         * 速度
         */
        public static final String SPEED = "speed";

        /**
         * 运动信息
         */
        public static final String MOTION_INFO = "motionInfo";

        /**
         * 频道
         */
        public static final String CHANNEL = "channel";

        /**
         * 静音状态
         */
        public static final String MUTE_STATE = "muteState";

        /**
         * 音量
         */
        public static final String VOLUME = "volume";

        /**
         * 吸力
         */
        public static final String SUCTION = "suction";

        /**
         * 水位
         */
        public static final String WATER_LEVEL = "waterLevel";

        /**
         * 位置
         */
        public static final String LOCATION = "location";

        /**
         * 工作状态
         */
        public static final String WORK_STATE = "workState";

        /**
         * 暖度等级
         */
        public static final String WARMTH_LEVEL = "warmthLevel";


    }

    /**
     * 物联网设备功能服务标识
     *
     * @author huangdayu
     */
    public static final class Services {

        /**
         * 打开
         */
        public static final String TURN_ON = "turnOn";

        /**
         * 关闭
         */
        public static final String TURN_OFF = "turnOff";

        /**
         * 定时打开
         */
        public static final String TIMING_TURN_ON = "timingTurnOn";

        /**
         * 定时关闭
         */
        public static final String TIMING_TURN_OFF = "timingTurnOff";

        /**
         * 暂停
         */
        public static final String PAUSE = "pause";

        /**
         * 继续
         */
        public static final String CONTINUES = "continues";

        /**
         * 设置颜色
         */
        public static final String SET_COLOR = "setColor";

        /**
         * 设置灯光色温
         */
        public static final String SET_COLOR_TEMPERATURE = "setColorTemperature";

        /**
         * 增高灯光色温
         */
        public static final String INCREMENT_COLOR_TEMPERATURE = "incrementColorTemperature";

        /**
         * 降低灯光色温
         */
        public static final String DECREMENT_COLOR_TEMPERATURE = "decrementColorTemperature";

        /**
         * 设置灯光亮度
         */
        public static final String SET_BRIGHTNESS_PERCENTAGE = "setBrightnessPercentage";

        /**
         * 调亮灯光
         */
        public static final String INCREMENT_BRIGHTNESS_PERCENTAGE = "incrementBrightnessPercentage";

        /**
         * 调暗灯光
         */
        public static final String DECREMENT_BRIGHTNESS_PERCENTAGE = "decrementBrightnessPercentage";

        /**
         * 设置功率
         */
        public static final String SET_POWER = "setPower";

        /**
         * 增大功率
         */
        public static final String INCREMENT_POWER = "incrementPower";

        /**
         * 减小功率
         */
        public static final String DECREMENT_POWER = "decrementPower";

        /**
         * 升高温度
         */
        public static final String INCREMENT_TEMPERATURE = "incrementTemperature";

        /**
         * 降低温度
         */
        public static final String DECREMENT_TEMPERATURE = "decrementTemperature";

        /**
         * 设置温度
         */
        public static final String SET_TEMPERATURE = "setTemperature";

        /**
         * 增加风速
         */
        public static final String INCREMENT_FAN_SPEED = "incrementFanSpeed";

        /**
         * 减小风速
         */
        public static final String DECREMENT_FAN_SPEED = "decrementFanSpeed";

        /**
         * 设置风速
         */
        public static final String SET_FAN_SPEED = "setFanSpeed";

        /**
         * 设置档位
         */
        public static final String SET_GEAR = "setGear";

        /**
         * 设置模式
         */
        public static final String SET_MODE = "setMode";

        /**
         * 取消设置的模式
         */
        public static final String UNSET_MODE = "unSetMode";

        /**
         * 定时设置模式
         */
        public static final String TIMING_SET_MODE = "timingSetMode";

        /**
         * 定时取消设置的模式
         */
        public static final String TIMING_UNSET_MODE = "timingUnsetMode";

        /**
         * 调高音量
         */
        public static final String INCREMENT_VOLUME = "incrementVolume";

        /**
         * 调低音量
         */
        public static final String DECREMENT_VOLUME = "decrementVolume";

        /**
         * 设置音量
         */
        public static final String SET_VOLUME = "setVolume";

        /**
         * 设置静音状态
         */
        public static final String SET_VOLUME_MUTE = "setVolumeMute";

        /**
         * 上一个频道
         */
        public static final String DECREMENT_TV_CHANNEL = "decrementTVChannel";

        /**
         * 下一个频道
         */
        public static final String INCREMENT_TV_CHANNEL = "incrementTVChannel";

        /**
         * 设置频道
         */
        public static final String SET_TV_CHANNEL = "setTVChannel";

        /**
         * 返回上个频道
         */
        public static final String RETURN_TV_CHANNEL = "returnTVChannel";

        /**
         * 开始充电
         */
        public static final String CHARGE_TURN_ON = "chargeTurnOn";

        /**
         * 停止充电
         */
        public static final String CHARGE_TURN_OFF = "chargeTurnOff";

        /**
         * 查询开关状态
         */
        public static final String GET_TURN_ON_STATE = "getTurnOnState";

        /**
         * 查询油量
         */
        public static final String GET_OIL_CAPACITY = "getOilCapacity";

        /**
         * 查询电量
         */
        public static final String GET_ELECTRICITY_CAPACITY = "getElectricityCapacity";

        /**
         * 上锁/解锁
         */
        public static final String SET_LOCK_STATE = "setLockState";

        /**
         * 查询锁状态
         */
        public static final String GET_LOCK_STATE = "getLockState";

        /**
         * 设置吸力
         */
        public static final String SET_SUCTION = "setSuction";

        /**
         * 设置水量
         */
        public static final String SET_WATER_LEVEL = "setWaterLevel";

        /**
         * 设置清扫位置
         */
        public static final String SET_CLEANING_LOCATION = "setCleaningLocation";

        /**
         * 执行自定义复杂动作
         */
        public static final String SET_COMPLEX_ACTIONS = "setComplexActions";

        /**
         * 设置移动方向
         */
        public static final String SET_DIRECTION = "setDirection";

        /**
         * 打印
         */
        public static final String SUBMIT_PRINT = "submitPrint";

        /**
         * 查询PM2.5
         */
        public static final String GET_AIR_PM25 = "getAirPM25";

        /**
         * 查询PM10
         */
        public static final String GET_AIR_PM10 = "getAirPM10";

        /**
         * 查询二氧化碳含量
         */
        public static final String GET_CO2_QUANTITY = "getCO2Quantity";

        /**
         * 查询空气质量
         */
        public static final String GET_AIR_QUALITY_INDEX = "getAirQualityIndex";

        /**
         * 查询温度（当前温度和目标温度）
         */
        public static final String GET_TEMPERATURE = "getTemperature";

        /**
         * 查询当前温度
         */
        public static final String GET_TEMPERATURE_READING = "getTemperatureReading";

        /**
         * 查询目标温度
         */
        public static final String GET_TARGET_TEMPERATURE = "getTargetTemperature";

        /**
         * 查询湿度
         */
        public static final String GET_HUMIDITY = "getHumidity";

        /**
         * 查询目标湿度
         */
        public static final String GET_TARGET_HUMIDITY = "getTargetHumidity";

        /**
         * 查询水质
         */
        public static final String GET_WATER_QUALITY = "getWaterQuality";

        /**
         * 查询设备所有状态
         */
        public static final String GET_STATE = "getState";

        /**
         * 查询剩余时间
         */
        public static final String GET_TIME_LEFT = "getTimeLeft";

        /**
         * 查询运行状态
         */
        public static final String GET_RUNNING_STATUS = "getRunningStatus";

        /**
         * 查询运行时间
         */
        public static final String GET_RUNNING_TIME = "getRunningTime";

        /**
         * 查询设备所在位置
         */
        public static final String GET_LOCATION = "getLocation";

        /**
         * 设备定时
         */
        public static final String SET_TIMER = "setTimer";

        /**
         * 取消设备定时
         */
        public static final String TIMING_CANCEL = "timingCancel";

        /**
         * 设备复位
         */
        public static final String RESET = "reset";

        /**
         * 升高高度
         */
        public static final String INCREMENT_HEIGHT = "incrementHeight";

        /**
         * 降低高度
         */
        public static final String DECREMENT_HEIGHT = "decrementHeight";

        /**
         * 设置摆风角度
         */
        public static final String SET_SWING_ANGLE = "setSwingAngle";

        /**
         * 查询风速
         */
        public static final String GET_FAN_SPEED = "getFanSpeed";

        /**
         * 设置湿度模式
         */
        public static final String SET_HUMIDITY = "setHumidity";

        /**
         * 增大湿度
         */
        public static final String INCREMENT_HUMIDITY = "incrementHumidity";

        /**
         * 降低湿度
         */
        public static final String DECREMENT_HUMIDITY = "decrementHumidity";

        /**
         * 增大雾量
         */
        public static final String INCREMENT_MIST = "incrementMist";

        /**
         * 减效雾量
         */
        public static final String DECREMENT_MIST = "decrementMist";

        /**
         * 设置雾量
         */
        public static final String SET_MIST = "setMist";

        /**
         * 设备启动
         */
        public static final String START_UP = "startUp";

        /**
         * 设置电梯楼层
         */
        public static final String SET_FLOOR = "setFloor";

        /**
         * 电梯按下
         */
        public static final String DECREMENT_FLOOR = "decrementFloor";

        /**
         * 电梯按上
         */
        public static final String INCREMENT_FLOOR = "incrementFloor";

        /**
         * 增加速度
         */
        public static final String INCREMENT_SPEED = "incrementSpeed";

        /**
         * 降低速度
         */
        public static final String DECREMENT_SPEED = "decrementSpeed";

        /**
         * 设置速度
         */
        public static final String SET_SPEED = "setSpeed";

        /**
         * 获取速度
         */
        public static final String GET_SPEED = "getSpeed";

        /**
         * 获取跑步信息
         */
        public static final String GET_MOTION_INFO = "getMotionInfo";

        /**
         * 打开灶眼
         */
        public static final String TURN_ON_BURNER = "turnOnBurner";

        /**
         * 关闭灶眼
         */
        public static final String TURN_OFF_BURNER = "turnOffBurner";

        /**
         * 定时打开灶眼
         */
        public static final String TIMING_TURN_ON_BURNER = "timingTurnOnBurner";

        /**
         * 定时关闭灶眼
         */
        public static final String TIMING_TURN_OFF_BURNER = "timingTurnOffBurner";


        // 功能性操作
        /**
         * 重置设备
         */
        public static final String RESET_DEVICE = "resetDevice";


        /**
         * 继续设备
         */
        public static final String CONTINUE = "continue";

        /**
         * 增加参数值
         */
        public static final String INCREMENT_PARAMETER = "incrementParameter";

        /**
         * 减少参数值
         */
        public static final String DECREMENT_PARAMETER = "decrementParameter";

        // 设备特定功能性操作
        /**
         * 添加指纹
         */
        public static final String ADD_FINGERPRINT = "addFingerprint";

        /**
         * 删除指纹
         */
        public static final String REMOVE_FINGERPRINT = "removeFingerprint";

        /**
         * 添加密码
         */
        public static final String ADD_PASSWORD = "addPassword";

        /**
         * 删除密码
         */
        public static final String REMOVE_PASSWORD = "removePassword";

        /**
         * 添加人脸
         */
        public static final String ADD_FACE = "addFace";

        /**
         * 删除人脸
         */
        public static final String REMOVE_FACE = "removeFace";

        /**
         * 开启/关闭语音控制
         */
        public static final String TOGGLE_VOICE_CONTROL = "toggleVoiceControl";

        /**
         * 切换输入源
         */
        public static final String SWITCH_INPUT_SOURCE = "switchInputSource";

        /**
         * 开启/关闭儿童锁
         */
        public static final String TOGGLE_CHILD_LOCK = "toggleChildLock";

        /**
         * 开启警报
         */
        public static final String TURN_ON_ALARM = "turnOnAlarm";

        /**
         * 关闭警报
         */
        public static final String TURN_OFF_ALARM = "turnOffAlarm";

        /**
         * 开启录像
         */
        public static final String START_RECORDING = "startRecording";

        /**
         * 关闭录像
         */
        public static final String STOP_RECORDING = "stopRecording";

        /**
         * 开启/关闭夜视模式
         */
        public static final String TOGGLE_NIGHT_VISION = "toggleNightVision";

        /**
         * 发送红外信号
         */
        public static final String SEND_INFRARED_SIGNAL = "sendInfraredSignal";

        /**
         * 学习红外信号
         */
        public static final String LEARN_INFRARED_SIGNAL = "learnInfraredSignal";

        /**
         * 删除学习的红外信号
         */
        public static final String REMOVE_LEARNED_INFRARED_SIGNAL = "removeLearnedInfraredSignal";

        /**
         * 开启/关闭红外学习模式
         */
        public static final String TOGGLE_IR_LEARNING_MODE = "toggleIrLearningMode";


    }

    /**
     * 物联网产品类型
     *
     * @author huangdayu
     */
    public static final class Products {

        /**
         * 电灯
         */
        public static final String LIGHT = "LIGHT";

        /**
         * 空调
         */
        public static final String AIR_CONDITION = "AIR_CONDITION";

        /**
         * 窗帘
         */
        public static final String CURTAIN = "CURTAIN";

        /**
         * 窗纱
         */
        public static final String CURT_SIMP = "CURT_SIMP";

        /**
         * 插座
         */
        public static final String SOCKET = "SOCKET";

        /**
         * 开关
         */
        public static final String SWITCH = "SWITCH";

        /**
         * 冰箱
         */
        public static final String FRIDGE = "FRIDGE";

        /**
         * 净水器
         */
        public static final String WATER_PURIFIER = "WATER_PURIFIER";

        /**
         * 加湿器
         */
        public static final String HUMIDIFIER = "HUMIDIFIER";

        /**
         * 除湿器
         */
        public static final String DEHUMIDIFIER = "DEHUMIDIFIER";

        /**
         * 电磁炉
         */
        public static final String INDUCTION_COOKER = "INDUCTION_COOKER";

        /**
         * 空气净化器
         */
        public static final String AIR_PURIFIER = "AIR_PURIFIER";

        /**
         * 洗衣机
         */
        public static final String WASHING_MACHINE = "WASHING_MACHINE";

        /**
         * 热水器
         */
        public static final String WATER_HEATER = "WATER_HEATER";

        /**
         * 燃气灶
         */
        public static final String GAS_STOVE = "GAS_STOVE";

        /**
         * 电视机
         */
        public static final String TV_SET = "TV_SET";

        /**
         * 网络盒子
         */
        public static final String OTT_BOX = "OTT_BOX";

        /**
         * 油烟机
         */
        public static final String RANGE_HOOD = "RANGE_HOOD";

        /**
         * 电风扇
         */
        public static final String FAN = "FAN";

        /**
         * 投影仪
         */
        public static final String PROJECTOR = "PROJECTOR";

        /**
         * 扫地机器人
         */
        public static final String SWEEPING_ROBOT = "SWEEPING_ROBOT";

        /**
         * 热水壶
         */
        public static final String KETTLE = "KETTLE";

        /**
         * 微波炉
         */
        public static final String MICROWAVE_OVEN = "MICROWAVE_OVEN";

        /**
         * 压力锅
         */
        public static final String PRESSURE_COOKER = "PRESSURE_COOKER";

        /**
         * 电饭煲
         */
        public static final String RICE_COOKER = "RICE_COOKER";

        /**
         * 破壁机
         */
        public static final String HIGH_SPEED_BLENDER = "HIGH_SPEED_BLENDER";

        /**
         * 新风机
         */
        public static final String AIR_FRESHER = "AIR_FRESHER";

        /**
         * 晾衣架
         */
        public static final String CLOTHES_RACK = "CLOTHES_RACK";

        /**
         * 烤箱设备
         */
        public static final String OVEN = "OVEN";

        /**
         * 蒸烤箱
         */
        public static final String STEAM_OVEN = "STEAM_OVEN";

        /**
         * 蒸箱
         */
        public static final String STEAM_BOX = "STEAM_BOX";

        /**
         * 电暖器
         */
        public static final String HEATER = "HEATER";

        /**
         * 开窗器
         */
        public static final String WINDOW_OPENER = "WINDOW_OPENER";

        /**
         * 摄像头
         */
        public static final String WEBCAM = "WEBCAM";

        /**
         * 相机
         */
        public static final String CAMERA = "CAMERA";

        /**
         * 机器人
         */
        public static final String ROBOT = "ROBOT";

        /**
         * 打印机
         */
        public static final String PRINTER = "PRINTER";

        /**
         * 饮水机
         */
        public static final String WATER_COOLER = "WATER_COOLER";

        /**
         * 鱼缸
         */
        public static final String FISH_TANK = "FISH_TANK";

        /**
         * 浇花器
         */
        public static final String WATERING_DEVICE = "WATERING_DEVICE";

        /**
         * 机顶盒
         */
        public static final String SET_TOP_BOX = "SET_TOP_BOX";

        /**
         * 香薰机
         */
        public static final String AROMATHERAPY_MACHINE = "AROMATHERAPY_MACHINE";

        /**
         * DVD
         */
        public static final String DVD = "DVD";

        /**
         * 鞋柜
         */
        public static final String SHOE_CABINET = "SHOE_CABINET";

        /**
         * 走步机
         */
        public static final String WALKING_MACHINE = "WALKING_MACHINE";

        /**
         * 跑步机
         */
        public static final String TREADMILL = "TREADMILL";

        /**
         * 床
         */
        public static final String BED = "BED";

        /**
         * 浴霸
         */
        public static final String YUBA = "YUBA";

        /**
         * 花洒
         */
        public static final String SHOWER = "SHOWER";

        /**
         * 浴缸
         */
        public static final String BATHTUB = "BATHTUB";

        /**
         * 消毒柜
         */
        public static final String DISINFECTION_CABINET = "DISINFECTION_CABINET";

        /**
         * 洗碗机
         */
        public static final String DISHWASHER = "DISHWASHER";

        /**
         * 沙发品类
         */
        public static final String SOFA = "SOFA";

        /**
         * 门铃
         */
        public static final String DOOR_BELL = "DOOR_BELL";

        /**
         * 电梯
         */
        public static final String ELEVATOR = "ELEVATOR";

        /**
         * 体重秤
         */
        public static final String WEIGHT_SCALE = "WEIGHT_SCALE";

        /**
         * 体脂秤
         */
        public static final String BODY_FAT_SCALE = "BODY_FAT_SCALE";

        /**
         * 壁挂炉
         */
        public static final String WALL_HUNG_GAS_BOILER = "WALL_HUNG_GAS_BOILER";


    }

    /**
     * method常量
     *
     * things.${methodType}.${identifier}.${methodAction}
     * thing.service.identifier.request
     * thing.service.identifier.response
     * thing.event.identifier.post
     * thing.properties.all.set
     * thing.properties.all.get
     * thing.properties.all.post
     */
    public static class Methods {
        public static final String THINGS_IDENTIFIER = "identifier";
        public static final String THINGS_SERVICE_REQUEST = "thing.service.identifier.request";
        public static final String THINGS_SERVICE_RESPONSE = "thing.service.identifier.response";
        public static final String THINGS_EVENT_POST = "thing.event.identifier.post";
        public static final String THINGS_PROPERTIES_SET = "thing.properties.all.set";
        public static final String THINGS_PROPERTIES_GET = "thing.properties.all.get";
        public static final String THINGS_PROPERTIES_POST = "thing.properties.all.post";
        public static final String THINGS_SYSTEM_SET = "thing.system.identifier.set";
        public static final String THINGS_SYSTEM_GET = "thing.system.identifier.get";
        public static final String THINGS_SYSTEM_POST = "thing.system.identifier.post";
    }

    public static class MethodActions {
        public static final String THINGS_ACTION_SET = "set";
        public static final String THINGS_ACTION_GET = "get";
        public static final String THINGS_ACTION_POST = "post";
        public static final String THINGS_ACTION_REQUEST = "request";
        public static final String THINGS_ACTION_RESPONSE = "response";
    }

    /**
     * 错误码常量
     */
    public static class ErrorCodes {
        public static final String SUCCESS = "200";
        public static final String CREATED = "201";
        public static final String ACCEPTED = "202";
        public static final String NO_CONTENT = "204";
        public static final String NOT_MODIFIED = "304";
        public static final String BAD_REQUEST = "400";
        public static final String UNAUTHORIZED = "401";
        public static final String FORBIDDEN = "403";
        public static final String NOT_FOUND = "404";
        public static final String NOT_ACCEPTABLE = "406";
        public static final String UNSUPPORTED_MEDIA_TYPE = "415";
        public static final String UNPROCESSABLE_ENTITY = "422";
        public static final String ERROR = "500";
        public static final String NOT_IMPLEMENTED = "501";
        public static final String BAD_GATEWAY = "502";
        public static final String SERVICE_UNAVAILABLE = "503";
        public static final String GATEWAY_TIMEOUT = "504";
        public static final String HTTP_VERSION_NOT_SUPPORTED = "505";
    }

    /**
     * 物模型传输协议
     */
    public static class Protocol {

        public static final String MQTT = "mqtt";

        public static final String RESTFUL = "restful";

        public static final String AMQP = "amqp";

        public static final String COAP = "coap";

        public static final String WEBSOCKET = "websocket";
    }

    public static class SystemMethod {
        public static final String SYSTEM_METHOD_START_WITH = "thing.system.";
        public static final String SYSTEM_METHOD_LOGIN = "login";
        public static final String SYSTEM_METHOD_LOGOUT = "logout";
        public static final String SYSTEM_METHOD_DSL = "dsl";
        public static final String SYSTEM_METHOD_CONFIG = "config";
    }
}
