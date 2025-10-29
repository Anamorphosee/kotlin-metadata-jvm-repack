import dummy.internal.extensions.MetadataExtensions;
import dummy.jvm.internal.JvmMetadataExtensions;

module dev.reformator.kotlinmetadatajvmrepack {
    requires kotlin.stdlib;

    exports dev.reformator.kmetarepack;
    exports dev.reformator.kmetarepack.jvm;

    uses MetadataExtensions;
    provides MetadataExtensions with JvmMetadataExtensions;
}
