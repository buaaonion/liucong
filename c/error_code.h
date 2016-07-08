#ifndef _error_code_
#define _error_code_
#include "BStar.h"
#include "List.h"
#include "Array.h"
typedef enum {
EPERM,
ENOENT,
ESRCH,
EINTR,
EIO,
ENXIO,
E2BIG,
ENOEXEC,
EBADF,
ECHILD,
EAGAIN,
ENOMEM,
EACCES,
EFAULT,
ENOTBLK,
EBUSY,
EEXIST,
EXDEV,
ENODEV,
ENOTDIR,
EISDIR,
EINVAL,
ENFILE,
EMFILE,
ENOTTY,
ETXTBSY,
EFBIG,
ENOSPC,
ESPIPE,
EROFS,
EMLINK,
EPIPE,
EDOM,
ERANGE,
EDEADLK,
ENAMETOOLONG,
ENOLCK,
ENOSYS,
ENOTEMPTY,
ELOOP,
EWOULDBLOCK,
ENOMSG,
EIDRM,
ECHRNG,
EL2NSYNC,
EL3HLT,
EL3RST,
ELNRNG,
EUNATCH,
ENOCSI,
EL2HLT,
EBADE,
EBADR,
EXFULL,
ENOANO,
EBADRQC,
EBADSLT,
EDEADLOCK,
EBFONT,
ENOSTR,
ENODATA,
ETIME,
ENOSR,
ENONET,
ENOPKG,
EREMOTE,
ENOLINK,
EADV,
ESRMNT,
ECOMM,
EPROTO,
EMULTIHOP,
EDOTDOT,
EBADMSG,
EOVERFLOW,
ENOTUNIQ,
EBADFD,
EREMCHG,
ELIBACC,
ELIBBAD,
ELIBSCN,
ELIBMAX,
ELIBEXEC,
EILSEQ,
ERESTART,
ESTRPIPE,
EUSERS,
ENOTSOCK,
EDESTADDRREQ,
EMSGSIZE,
EPROTOTYPE,
ENOPROTOOPT,
EPROTONOSUPPORT,
ESOCKTNOSUPPORT,
EOPNOTSUPP,
EPFNOSUPPORT,
EAFNOSUPPORT,
EADDRINUSE,
EADDRNOTAVAIL,
ENETDOWN,
ENETUNREACH,
ENETRESET,
ECONNABORTED,
ECONNRESET,
ENOBUFS,
EISCONN,
ENOTCONN,
ESHUTDOWN,
ETOOMANYREFS,
ETIMEDOUT,
ECONNREFUSED,
EHOSTDOWN,
EHOSTUNREACH,
EALREADY,
EINPROGRESS,
ESTALE,
EUCLEAN,
ENOTNAM,
ENAVAIL,
EISNAM,
EREMOTEIO,
EDQUOT,
ENOMEDIUM,
EMEDIUMTYPE,
ECANCELED,
ENOKEY,
EKEYEXPIRED,
EKEYREVOKED,
EKEYREJECTED,
EACTIVE = 132,
ENOIPC,
ENOCAP,
ENOUTCB,
ENOMAP,
ENOIRQ,
EABORT,
ENOCHILD,
ENOTDAC = 150,
ENOTMAC,
ERIUSE,
ENACTIVE,
PAGE_NOT_ALIGNED,
PAGE_ZERO_ADDRESS,
PAGE_PAGING_NOT_ENABLED,
PAGE_MAP_NOT_ALLOWED,
PAGE_NOT_EXIST,
PAGE_FAIL,
OK
} errorCode;
