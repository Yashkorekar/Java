package DataStructuresBasics;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Beginner-first binary tree drills using a Binary Search Tree example.
 *
 * Run:
 *   javac .\DataStructuresBasics\BinaryTreeBasicsDemo.java
 *   java DataStructuresBasics.BinaryTreeBasicsDemo
 */
public class BinaryTreeBasicsDemo {

    static final class Node {
        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        Node root = null;
        int[] values = {50, 30, 70, 20, 40, 60, 80};

        for (int value : values) {
            root = insert(root, value);
        }

        System.out.println("=== Binary Tree / BST basics ===");
        System.out.print("Inorder traversal: ");
        inorder(root);
        System.out.println();

        System.out.print("Preorder traversal: ");
        preorder(root);
        System.out.println();

        System.out.print("Postorder traversal: ");
        postorder(root);
        System.out.println();

        System.out.print("Level-order traversal: ");
        levelOrder(root);
        System.out.println();

        System.out.println("Contains 60: " + contains(root, 60));
        System.out.println("Contains 99: " + contains(root, 99));
        System.out.println("Height of tree: " + height(root));
        System.out.println("Total nodes: " + countNodes(root));
        System.out.println("Minimum value: " + minValue(root));
        System.out.println("Maximum value: " + maxValue(root));
        System.out.println();

        commonInterviewChecklist();
    }

    static Node insert(Node root, int value) {
        if (root == null) {
            return new Node(value);
        }

        if (value < root.data) {
            root.left = insert(root.left, value);
        } else if (value > root.data) {
            root.right = insert(root.right, value);
        }

        return root;
    }

    static boolean contains(Node root, int value) {
        if (root == null) {
            return false;
        }
        if (root.data == value) {
            return true;
        }
        if (value < root.data) {
            return contains(root.left, value);
        }
        return contains(root.right, value);
    }

    static void inorder(Node root) {
        if (root == null) {
            return;
        }
        inorder(root.left);
        System.out.print(root.data + " ");
        inorder(root.right);
    }

    static void preorder(Node root) {
        if (root == null) {
            return;
        }
        System.out.print(root.data + " ");
        preorder(root.left);
        preorder(root.right);
    }

    static void postorder(Node root) {
        if (root == null) {
            return;
        }
        postorder(root.left);
        postorder(root.right);
        System.out.print(root.data + " ");
    }

    static void levelOrder(Node root) {
        if (root == null) {
            return;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            System.out.print(current.data + " ");

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }
    }

    static int height(Node root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(height(root.left), height(root.right));
    }

    static int countNodes(Node root) {
        if (root == null) {
            return 0;
        }
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    static int minValue(Node root) {
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.data;
    }

    static int maxValue(Node root) {
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    private static void commonInterviewChecklist() {
        System.out.println("=== Binary tree interview checklist ===");
        System.out.println("- A binary tree node has at most two children: left and right.");
        System.out.println("- In a BST, left subtree values are smaller and right subtree values are larger.");
        System.out.println("- Inorder traversal of a BST prints values in sorted order.");
        System.out.println("- Preorder: root, left, right.");
        System.out.println("- Postorder: left, right, root.");
        System.out.println("- Level-order uses a queue.");
        System.out.println("- Height means the longest path from root to leaf in node count here.");
    }
}
