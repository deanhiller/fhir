licenses(["notice"])

package(
    default_visibility = ["//visibility:public"],
)

load("//bazel:proto.bzl", "fhir_proto_library")
load("//bazel:protogen.bzl", "gen_fhir_definitions_and_protos")

gen_fhir_definitions_and_protos(
    name = "test",
    extensions = [
        "test_extensions.prototxt",
    ],
    package_deps = [
        "//spec:uscore-r4-package",
    ],
    package_info = "test_package_info.prototxt",
    profiles = [
        "test_profiles.prototxt",
    ],
    separate_extensions = True,
)

fhir_proto_library(
    srcs = [
        "test.proto",
        "test_extensions.proto",
    ],
    proto_deps = [
        "//proto:annotations_proto",
        "//proto/r4:codes_proto",
        "//proto/r4:datatypes_proto",
        "//proto/r4:extensions_proto",
        "//proto/r4:resources_proto",
    ],
    proto_library_prefix = "test",
)

filegroup(
    name = "testdata",
    srcs = glob([
        "*.prototxt",
        "*.json",
    ]),
)