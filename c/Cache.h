#ifndef _Cache_
#define _Cache_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "arch_types.h"
#define CACHE_INHIBIT 0x40
typedef enum {
CPU_CACHE_INS,
CPU_CACHE_DAT,
CPU_CACHE_ALL
} T_CPUCacheTypes;
