/* Create a singly list with dummy head node */
#include "List.h"
#include <stdio.h>
#include <string.h>

List_Position List_getPos(List* list, BStar_Object e, BStar_Equal equal) {
	List_Position pos = list->head;
	while (pos) {
		if (e == pos->elemAddr || (e != NULL && equal(e, pos->elemAddr))) {
			return pos;
		} else {
			pos = pos->next;
		}
	}
	return NULL;
}

List_Position List_priorPos(List* list, BStar_Object e, BStar_Equal equal) {
	List_Position prior = list->head;
	List_Position pos = list->head->next;
	while (pos) {
		if (e == pos->elemAddr || (e != NULL && equal(e, pos->elemAddr))) {
			return prior;
		} else {
			prior = pos;
			pos = pos->next;
		}
	}
	return NULL;
}

List* List_new(BStar_Equal elemEqual) {
	List* list = NULL;

	if ((list = malloc(sizeof(struct List))) == NULL) {
		fprintf(stderr, "Out of memory!\n");
		exit(1);
	}

	if ((list->head = malloc(sizeof(struct List_Node))) == NULL) {
		fprintf(stderr, "Out of memory!\n");
		exit(1);
	}

	list->head->elemAddr = NULL;

	if ((list->head->next = malloc(sizeof(struct List_Node))) == NULL) {
		fprintf(stderr, "Out of memory!\n");
		exit(1);
	}

	list->head->next->next = NULL;
	list->head->next->elemAddr = NULL;

	list->elemEqual = elemEqual;
	list->size = 0;

	return list;
}

/* Dispose the list */
void List_destroy(List* list) {
	List_clear(list);
	free(list->head->next);
	free(list->head);
	list->size = -1;
	free(list);
}

/* Insert an element with given position pos in the List list */
proposition List_quickInsert(List* list, List_Position pos, BStar_Object e) {
	List_PtrToNode node;
	BStar_Object elemAddr = e;
	if ((node = malloc(sizeof(struct List_Node))) == NULL) {
		fprintf(stderr, "Out of memory!\n");
		exit(1);
	}

	node->elemAddr = elemAddr;
	node->next = pos->next;
	pos->next = node;
	list->size++;
	return true;
}
/*
 List* List_join(List* list1, List* list2, BStar_Equal equal) {
 List * list = List_new();
 List_Position pos = list1->head;
 while (pos) {
 if (List_get(list2, pos->elemAddr, equal)) {
 List_add(list, pos->elemAddr);
 }
 pos=pos->next;
 }
 return list;
 }
 */
/* Insert an element with given position pos in the List list */
proposition List_insert(List* list, BStar_Object p, BStar_Object e) {

	List_Position pos = List_getPos(list, p, list->elemEqual);
	if (pos) {
		return List_quickInsert(list, pos, e);
	} else {
		return false;
	}
}

void List_add(List* list, BStar_Object e) {
	List_insert(list, NULL, e);
}

List* List_add_List(List* list, List* list2) {
	List_PtrToNode node = list2->head;
	while(node) {
		if(!List_get(list, node)) {
			List_add(list, node);
		}
		node = node->next;
	}
	return list;
}

List* List_delete_List(List* list, List* list2) {
	List_PtrToNode node = list2->head->next;
	while(node) {
		List_delete(list, node);
	}
	return list;
}

/* Delete the first node in List list when x is found */
proposition List_delete(List* list, BStar_Object e) {
//	List_Position tmp = NULL;
//	List_Position pos = List_priorPos(list, e, list->elemEqual);

//	if (pos) {
//		if (pos->next) {
//			tmp = pos->next;
//			pos->next = tmp->next;
//			free(tmp);
//			list->size--;
//			return true;
//		}
//	}
//	return false;
	return List_delete2(list, e, list->elemEqual);
}

/* Delete the first node in List list when x is found */
proposition List_delete2(List* list, BStar_Object e, BStar_Equal equal) {
	List_Position tmp = NULL;
	List_Position pos = List_priorPos(list, e, equal);

	if (pos) {
		if (pos->next) {
			tmp = pos->next;
			pos->next = tmp->next;
			free(tmp);
			list->size--;
			return true;
		}
	}
	return false;
}

int List_isEmpty(List* list) {
	return /*list == NULL || list->head == NULL ||*/list->head->next->next
			== NULL;
}

/* Make the given list empty */
void List_clear(List* list) {
	List_PtrToNode first;
	if (!list) {
		fprintf(stderr, "Must use list_create first\n");
	} else {
		while (!List_isEmpty(list)) {
			first = list->head->next;
			list->head->next = first->next;
			free(first);
		}
	}
}

/* Return the postion of x in List list; NULL if not found */
BStar_Object List_get2(List* list, BStar_Object e, BStar_Equal equal) {
	List_Position pos = List_getPos(list, e, equal);
	if (pos) {
		return pos->elemAddr;
	} else {
		return NULL;
	}
}

/* Return the postion of x in List list; NULL if not found */
BStar_Object List_get(List* list, BStar_Object e) {
//	List_Position pos = List_getPos(list, e,list->elemEqual);
//	if (pos) {
//		return pos->elemAddr;
//	} else {
//		return NULL;
//	}
	return List_get2(list, e, list->elemEqual);
}

/*get anyone*/
BStar_Object List_get_poi(List* list, int poi) {
	int i = 0;
	List_Position ret = list->head->next;
	while(i < poi) {
		ret = ret->next;
	}
	return ret->elemAddr;
}

/* Return the postion of x in List list; NULL if not found */
BStar_Object List_prior(List* list, BStar_Object e) {
	List_Position pos = List_priorPos(list, e, list->elemEqual);
	if (pos) {
		return pos->elemAddr;
	} else {
		return NULL;
	}
}

/* Return the postion of x in List list; NULL if not found */
BStar_Object List_next(List* list, BStar_Object e) {
	List_Position pos = List_getPos(list, e, list->elemEqual);
	if (pos) {
		if (pos->next) {
			return pos->next->elemAddr;
		}
	}
	return NULL;
}

/* Return the element number of List list */
int List_size(List* list) {
	return list->size;
}

