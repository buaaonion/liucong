#ifndef _arch_types_
#define _arch_types_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#define CONFIG_CPU_PPC4080 1
#define CONFIG_CPU_PPC8641D 0
#define CONFIG_BATS 0
#define CONFIG_PAGES 1
#define CONFIG_KERNEL_START 0xc0000000
#define PAGE_SHIFT 12
#define PTE_SHIFT 10
#define PGDIR_SHIFT (PAGE_SHIFT+PTE_SHIFT)
#define PMD_SHIFT PGDIR_SHIFT
#define PAGE_SIZE (1<<PAGE_SHIFT)
#define PSE_PAGE_SIZE (4096*1024)
#define PGDIR_SIZE (1<<PGDIR_SHIFT)
#define PMD_SIZE (1<<PMD_SHIFT)
#define PAGE_MASK (!(PAGE_SIZE-1))
#define PGDIR_MASK (!(PGDIR_SIZE-1))
#define PMD_MASK (!(PMD_SIZE-1))
#define PTRS_PER_PTE (1<<PTE_SHIFT)
#define PTRS_PER_PMD 1
#define PTRS_PER_PGD (1<<(32-PGDIR_SHIFT))
#define ALIGNMENT 8
#define WORD_BITS 32
#define MAX_VIRTUAL_ADDR 0xffffffff
#define MAX_PHYSICAL_ADDR 0xffffffff
#define MAX_ID_VALUE 255
#define MAX_PRIO_VALUE 255
#define CONFIG_CONTAINER_NAMESIZE 256
#define MR_TOTAL 256
#define TASK_NOTIFY_SLOTS 256
#define MR_REST 256
typedef unsigned int T_ID;
typedef unsigned int P_ID;
typedef int INT32;
typedef unsigned int UINT32;
typedef short UINT16;
typedef unsigned char UINT8;
typedef char INT8;
typedef unsigned long long ULONG;
typedef unsigned long long UINT64;
typedef Array T_TaskName;
typedef Array T_ParName;
typedef Array T_IrqName;
extern ULONG MAX_THREAD_TICKStypedef struct T_TaskIds{
T_ID tid;
T_ID spid;
T_ID tgid;
};
