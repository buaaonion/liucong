#ifndef _SyscallDecls_
#define _SyscallDecls_
#include "BStar.h"
#include "List.h"
#include "Array.h"
#include "ThreadDecls.h"
typedef struct T_Kip{
UINT32 magic;
UINT16 version_rsrv;
UINT8 api_subversion;
UINT8 api_version;
UINT32 api_flags;
UINT32 container_control;
UINT32 time;
UINT32 irq_control;
UINT32 thread_control;
UINT32 ipc_control;
UINT32 map;
UINT32 ipc;
UINT32 capability_control;
UINT32 unmap;
UINT32 exchange_registers;
UINT32 thread_switch;
UINT32 schedule;
UINT32 getid;
UINT32 mutex_control;
UINT32 cache_control;
UINT32 arch_syscall0;
UINT32 arch_syscall1;
UINT32 arch_syscall2;
List UTCB_Set;
};
extern T_Kip *KIP;
