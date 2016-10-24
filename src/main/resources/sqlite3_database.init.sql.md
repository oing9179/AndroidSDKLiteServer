# 数据库设计

## 表 `repo_xml`

XML仓库，存放xml文件。

| name               | data type | flags                                | description                  |
|:-------------------|:----------|:-------------------------------------|:-----------------------------|
| id                 | INTEGER   | PRIMARY KEY, AUTOINCREMENT, NOT NULL |                              |
| name               | TEXT      | NOT NULL, UNIQUE                     | 仓库名称                     |
| data_creation      | INTEGER   | NOT NULL                             | 何时创建的仓库               |
| data_last_modified | INTEGER   | NOT NULL                             | 最后一次修改该仓库是什么时候 |

## 表 `repo_xml_file`

XML仓库里的文件，每个存放在xml仓库里的xml文件都要在这里记录。

| name              | data type | flags                                | description                          |
|:------------------|:----------|:-------------------------------------|:-------------------------------------|
| id                | INTEGER   | PRIMARY KEY, AUTOINCREMENT, NOT NULL |                                      |
| id_repo_xml       | INTEGER   | NOT NULL                             | fk_repo_xml_file__to__repo_xml_0     |
| file_name         | TEXT      | NOT NULL                             | xml文件名                            |
| url               | TEXT      | NOT NULL                             | 从哪里下载到的xml文件                |
| zip_sub_directory | TEXT      |                                      | 所有zip文件都放在zip仓库的这个目录下 |

外键约束:

| name                             | column      | target table | target column |
|:---------------------------------|:------------|:-------------|:--------------|
| fk_repo_xml_file__to__repo_xml_0 | id_repo_xml | repo_xml     | id            |

注释:

1. 列 `zip_sub_directory`
    1. 在 `/admin/repository/zip/repositoryName/file_completion` 内导出下载链接时候，自动在文件名前面添加该属性值，以此来避免同样的文件名导致的文件名冲突。
    2. 在 Android SDK Manager 请求新的仓库信息时（也就是从服务器下载xml），自动将xml文件内的链接前面加上该属性，以迎合 `第 i 条`.

## 表 `repo_zip`

ZIP仓库，存放zip文件。

| name               | data type | flags                                | description                      |
|:-------------------|:----------|:-------------------------------------|:---------------------------------|
| id                 | INTEGER   | PRIMARY KEY, AUTOINCREMENT, NOT NULL |                                  |
| id_repo_xml        | INTEGER   |                                      | fk_repo_xml_file__to__repo_xml_0 |
| name               | TEXT      | NOT NULL, UNIQUE                     | 仓库名称                         |
| date_creation      | INTEGER   | NOT NULL                             | 何时创建的仓库                   |
| date_last_modified | INTEGER   |                                      | 最后一次修改该仓库是什么时候     |

外键约束:

| name                             | column      | target table | target column |
|:---------------------------------|:------------|:-------------|:--------------|
| fk_repo_xml_file__to__repo_xml_0 | id_repo_xml | repo_xml     | id            |
