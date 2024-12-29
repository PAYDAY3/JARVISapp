import subprocess
import os
from datetime import datetime, timedelta
import random
import string
import math
import importlib
import inspect

class CommandPanel:
    def __init__(self):
        self.program_path = None
        self.output = ""
        self.tasks = []
        self.reminders = []
        self.python_tools = {}
        self.tool_names = {}
        self.scan_python_tools()

    def process_command(self, command):
        if command.startswith("打开"):
            return self.open_program(command[2:].strip())
        elif command.startswith("添加任务"):
            return self.add_task(command[4:].strip())
        elif command == "显示任务":
            return self.show_tasks()
        elif command.startswith("完成任务"):
            return self.complete_task(command[4:].strip())
        elif command.startswith("添加提醒"):
            return self.add_reminder(command[4:].strip())
        elif command == "显示提醒":
            return self.show_reminders()
        elif command.startswith("Python工具"):
            return self.python_tools_handler(command[9:].strip())
        elif command.startswith("添加工具"):
            return self.add_tool(command[4:].strip())
        elif command.startswith("修改工具名"):
            return self.modify_tool_name(command[5:].strip())
        else:
            return self.execute_command(command)

    def open_program(self, program_path):
        if os.path.exists(program_path):
            self.program_path = program_path
            return f"已打开 {program_path}"
        else:
            return f"程序 {program_path} 不存在"

    def execute_command(self, command):
        try:
            process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)
            stdout, stderr = process.communicate()
            
            if process.returncode == 0:
                self.output = stdout
                return stdout
            else:
                self.output = f"错误：{stderr}"
                return f"错误：{stderr}"
        except Exception as e:
            self.output = f"命令执行失败：{str(e)}"
            return f"命令执行失败：{str(e)}"

    def add_task(self, task):
        self.tasks.append(task)
        return f"已添加任务：{task}"

    def show_tasks(self):
        if not self.tasks:
            return "当前没有任务。"
        return "任务列表：\n" + "\n".join(f"{i+1}. {task}" for i, task in enumerate(self.tasks))

    def complete_task(self, task_index):
        try:
            index = int(task_index) - 1
            if 0 <= index < len(self.tasks):
                completed_task = self.tasks.pop(index)
                return f"已完成任务：{completed_task}"
            else:
                return "无效的任务索引。"
        except ValueError:
            return "请提供有效的任务编号。"

    def add_reminder(self, reminder_text):
        parts = reminder_text.split(" 在 ")
        if len(parts) != 2:
            return "无效的提醒格式。请使用'添加提醒 [内容] 在 [时间]'格式。"
        content, time_str = parts
        try:
            reminder_time = datetime.strptime(time_str, "%Y-%m-%d %H:%M")
            self.reminders.append((content, reminder_time))
            return f"已添加提醒：{content} 在 {time_str}"
        except ValueError:
            return "无效的时间格式。请使用'YYYY-MM-DD HH:MM'格式。"

    def show_reminders(self):
        if not self.reminders:
            return "当前没有提醒。"
        now = datetime.now()
        reminders_list = []
        for content, time in self.reminders:
            if time > now:
                time_diff = time - now
                if time_diff < timedelta(hours=1):
                    time_str = f"{time_diff.seconds // 60}分钟后"
                elif time_diff < timedelta(days=1):
                    time_str = f"{time_diff.seconds // 3600}小时后"
                else:
                    time_str = f"{time_diff.days}天后"
                reminders_list.append(f"{content} ({time_str})")
        return "提醒列表：\n" + "\n".join(reminders_list)

    def clear_output(self):
        self.output = ""
        return "输出已清除"

    def get_output(self):
        return self.output

    def python_tools_handler(self, tool_name):
        if tool_name in self.python_tools:
            return self.python_tools[tool_name]()
        elif tool_name in self.tool_names.values():
            for key, value in self.tool_names.items():
                if value == tool_name:
                    return self.python_tools[key]()
        else:
            return f"未知的Python工具: {tool_name}"

    def add_tool(self, tool_info):
        name, path = tool_info.split(',')
        if os.path.exists(path):
            module_name = os.path.basename(path)[:-3]
            spec = importlib.util.spec_from_file_location(module_name, path)
            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module)
            for func_name, func in inspect.getmembers(module, inspect.isfunction):
                if func_name.startswith('tool_'):
                    self.python_tools[func_name] = func
                    self.tool_names[func_name] = name
            return f"成功添加工具: {name}"
        else:
            return f"文件不存在: {path}"

    def modify_tool_name(self, tool_info):
        old_name, new_name = tool_info.split(',')
        for key, value in self.tool_names.items():
            if value == old_name:
                self.tool_names[key] = new_name
                return f"工具名称已修改: {old_name} -> {new_name}"
        return f"未找到工具: {old_name}"

    def scan_python_tools(self):
        tools_dir = os.path.join(os.path.dirname(__file__), 'tools')
        if os.path.exists(tools_dir):
            for filename in os.listdir(tools_dir):
                if filename.endswith('.py') and filename != '__init__.py':
                    module_name = filename[:-3]
                    module = importlib.import_module(f'jarvis.tools.{module_name}')
                    for name, obj in inspect.getmembers(module):
                        if inspect.isfunction(obj) and name.startswith('tool_'):
                            self.python_tools[name] = obj
                            self.tool_names[name] = name[5:].replace('_', ' ').title()

    def get_tool_list(self):
        return [f"{value}" for value in self.tool_names.values()]

    def generate_password(self):
        length = 12
        characters = string.ascii_letters + string.digits + string.punctuation
        password = ''.join(random.choice(characters) for _ in range(length))
        return f"生成的随机密码: {password}"

    def prime_numbers(self):
        def is_prime(n):
            if n < 2:
                return False
            for i in range(2, int(math.sqrt(n)) + 1):
                if n % i == 0:
                    return False
            return True
        
        primes = [num for num in range(2, 101) if is_prime(num)]
        return f"100以内的质数: {primes}"

    def fibonacci_sequence(self):
        def fib(n):
            if n <= 1:
                return n
            else:
                return fib(n-1) + fib(n-2)
        
        sequence = [fib(i) for i in range(10)]
        return f"斐波那契数列前10项: {sequence}"

    def unit_conversion(self):
        conversions = {
            "1 英里": f"{1.60934:.2f} 公里",
            "1 公斤": f"{2.20462:.2f} 磅",
            "1 英寸": f"{2.54:.2f} 厘米",
            "1 华氏度": f"{(32-32)*5/9:.2f} 摄氏度"
        }
        return "\n".join(f"{k} = {v}" for k, v in conversions.items())


command_panel = CommandPanel()

def process_command(command):
    return command_panel.process_command(command)

def clear_output():
    return command_panel.clear_output()

def get_output():
    return command_panel.get_output()

def get_tool_list():
    return command_panel.get_tool_list()

