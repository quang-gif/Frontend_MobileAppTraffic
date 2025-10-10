cmake_minimum_required(VERSION 3.22.1)

project("nativekeys")

add_library(
        native-keys
        SHARED
        native-keys.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        native-keys
        ${log-lib}
)

