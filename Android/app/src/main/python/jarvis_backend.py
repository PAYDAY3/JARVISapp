import datetime
import random

class JarvisInteractionBoard:
    def __init__(self):
        self.conversation_history = []
        self.commands = {
            "你好": self.greet,
            "时间": self.get_time,
            "天气": self.get_weather,
            "笑话": self.tell_joke,
            "帮助": self.get_help
        }

    def process_command(self, text):
        text = text.lower()
        self.conversation_history.append(("user", text))
        
        for key, func in self.commands.items():
            if key in text:
                response = func()
                self.conversation_history.append(("jarvis", response))
                return response
        
        response = "抱歉,我没有理解这个命令。你可以尝试说'帮助'来查看我能做什么。"
        self.conversation_history.append(("jarvis", response))
        return response

    def greet(self):
        return "你好！我是Jarvis,有什么我可以帮你的吗？"

    def get_time(self):
        current_time = datetime.datetime.now().strftime("%H:%M")
        return f"现在的时间是 {current_time}"

    def get_weather(self):
        return "抱歉,我目前无法获取实时天气数据。"

    def tell_joke(self):
        jokes = [
            "为什么电脑会生病？因为它们有病毒！",
            "我有一个删除重力的笑话,但它可能会飞过去。",
            "为什么程序员更喜欢黑暗模式？因为光明会消耗他们的能量。"
        ]
        return random.choice(jokes)

    def get_help(self):
        return "我可以帮你查看时间、讲笑话,或者问候你。只需要说出相关的关键词即可。"

    def get_conversation_history(self):
        return self.conversation_history

jarvis_board = JarvisInteractionBoard()

def process_command(text):
    return jarvis_board.process_command(text)

def get_conversation_history():
    return jarvis_board.get_conversation_history()

