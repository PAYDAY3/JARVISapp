class TaskManager:
    def __init__(self):
        self.tasks = []

    def add_task(self, task):
        self.tasks.append(task)
        return f"已添加任务：{task}"

    def list_tasks(self):
        if not self.tasks:
            return "当前没有任务。"
        return "当前任务列表：\n" + "\n".join(f"{i+1}. {task}" for i, task in enumerate(self.tasks))

    def remove_task(self, index):
        if 1 <= index <= len(self.tasks):
            task = self.tasks.pop(index - 1)
            return f"已删除任务：{task}"
        return "无效的任务索引。"

task_manager = TaskManager()

