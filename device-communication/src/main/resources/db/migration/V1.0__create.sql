CREATE TABLE IF NOT EXISTS public."deviceConfig"
(
    version           INT          not null,
    "tenantId"        VARCHAR(100) not null,
    "deviceId"        VARCHAR(100) not null,
    "cloudUpdateTime" VARCHAR(100) not null,
    "deviceAckTime"   VARCHAR(100),
    "binaryData"      VARCHAR      not null,

    PRIMARY KEY (version, "tenantId", "deviceId")
)