module dev.reformator.kotlinmetadatajvmrepack {
    requires kotlin.stdlib;

    exports dev.reformator.kmetarepack;
    exports dev.reformator.kmetarepack.jvm;

    uses dev.reformator.kmetarepack.internal.extensions.MetadataExtensions;
    provides dev.reformator.kmetarepack.internal.extensions.MetadataExtensions
            with dev.reformator.kmetarepack.jvm.internal.JvmMetadataExtensions;
}
